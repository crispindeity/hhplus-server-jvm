package kr.hhplus.be.server.common.lock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedisLocks(
    val keys: Array<String>,
    val waitSeconds: Long = 2,
    val leaseSeconds: Long = 0,
    val sortKeys: Boolean = false,
    val useMultiLock: Boolean = false
)
