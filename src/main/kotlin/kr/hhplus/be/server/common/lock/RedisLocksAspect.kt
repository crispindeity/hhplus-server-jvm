package kr.hhplus.be.server.common.lock

import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.spel.SpelException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component

@Aspect
@Component
class RedisLocksAspect(
    private val redisson: RedissonClient,
    private val parser: SpelExpressionParser,
    private val parameterNameDiscoverer: DefaultParameterNameDiscoverer
) {
    private val logger: Logger = Log.getLogger(RedisLocksAspect::class.java)

    companion object {
        private const val PREFIX = "lock:"
    }

    @Around("@annotation(kr.hhplus.be.server.common.lock.RedisLocks)")
    fun redisLocks(
        joinPoint: ProceedingJoinPoint,
        redisLocks: RedisLocks
    ): Any? =
        Log.logging(logger) { log ->
            log["method"] = "redisLocks()"
            val method: Method = (joinPoint.signature as MethodSignature).method
            log["joinPoint"] = method.name
            val context =
                MethodBasedEvaluationContext(
                    joinPoint.target,
                    method,
                    joinPoint.args,
                    parameterNameDiscoverer
                )

            val keys: List<String> = resolveAndNormalizeKeys(redisLocks, context)
            val lock: RLock = createLock(keys, redisLocks.useMultiLock)

            if (!tryAcquire(lock, redisLocks.waitSeconds, redisLocks.leaseSeconds)) {
                log["lock.keys"] = keys
                throw RedissonException(
                    code = ErrorCode.LOCK_ACQUIRE_TIME_OUT,
                    message = "keys: $keys"
                )
            }

            try {
                joinPoint.proceed()
            } finally {
                unlockSafely(lock)
            }
        }

    private fun resolveAndNormalizeKeys(
        redisLocks: RedisLocks,
        context: MethodBasedEvaluationContext
    ): List<String> {
        val raw: Set<String> =
            redisLocks.keys
                .flatMap { expression ->
                    when (val value = parser.parseExpression(expression).getValue(context)) {
                        null -> emptyList()
                        is String -> listOf(value)
                        is Collection<*> ->
                            value.map {
                                it?.toString() ?: throw SpelException(
                                    code = ErrorCode.NOT_VALUE_IN_EXPRESSION,
                                    message = expression
                                )
                            }

                        else -> throw SpelException(
                            code = ErrorCode.INVALID_RETURN_TYPE,
                            message = "$value"
                        )
                    }
                }.map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()

        if (raw.isEmpty()) {
            throw RedissonException(code = ErrorCode.KEYS_IS_EMPTY)
        }
        val normalized: List<String> = raw.map { if (it.startsWith(PREFIX)) it else "$PREFIX$it" }
        return if (redisLocks.sortKeys) normalized.sorted() else normalized
    }

    private fun createLock(
        keys: List<String>,
        useMultiLock: Boolean
    ): RLock =
        if (useMultiLock && keys.size > 1) {
            val locks: Array<RLock> = keys.map { redisson.getLock(it) }.toTypedArray()
            redisson.getMultiLock(*locks)
        } else {
            redisson.getLock(keys.first())
        }

    private fun tryAcquire(
        lock: RLock,
        waitSeconds: Long,
        leaseSeconds: Long
    ): Boolean =
        if (leaseSeconds == 0L) {
            lock.tryLock(waitSeconds, TimeUnit.SECONDS)
        } else {
            lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS)
        }

    private fun unlockSafely(lock: RLock) {
        if (lock.isHeldByCurrentThread) {
            runCatching { lock.unlock() }
                .onFailure {
                    throw RedissonException(
                        code = ErrorCode.FAILED_TO_UNLOCK,
                        message = "lock: ${lock.name}"
                    )
                }
        }
    }
}
