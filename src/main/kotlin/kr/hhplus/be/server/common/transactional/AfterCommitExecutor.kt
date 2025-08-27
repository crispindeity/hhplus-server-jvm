package kr.hhplus.be.server.common.transactional

import java.util.ArrayDeque
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
class AfterCommitExecutor {
    private val logger: Logger = Log.getLogger(AfterCommitExecutor::class.java)

    private data class TransactionScope(
        val afterCommitCallbacks: MutableList<Callback> = mutableListOf(),
        val afterRollbackCallbacks: MutableList<() -> Unit> = mutableListOf()
    )

    data class Callback(
        val priority: Int,
        val function: () -> Unit
    )

    private val transactionScopeStack = ThreadLocal.withInitial { ArrayDeque<TransactionScope>() }

    fun registerAfterCommit(
        priority: Int = 0,
        function: () -> Unit
    ) {
        currentScope().afterCommitCallbacks += Callback(priority, function)
    }

    fun registerAfterRollback(function: () -> Unit) {
        currentScope().afterRollbackCallbacks += function
    }

    fun pushTransactionScope() {
        transactionScopeStack.get().addLast(TransactionScope())
    }

    fun onTransactionCommit(isNewTransaction: Boolean = true) {
        val stack: ArrayDeque<TransactionScope> = transactionScopeStack.get()
        if (stack.isEmpty()) return
        val scope: TransactionScope = stack.removeLast()

        if (isNewTransaction) {
            scope.afterCommitCallbacks
                .sortedByDescending { it.priority }
                .forEach { safeRun(it.function) }
        } else {
            parentOrCreate(stack).also {
                it.afterCommitCallbacks.addAll(scope.afterCommitCallbacks)
                it.afterRollbackCallbacks.addAll(scope.afterRollbackCallbacks)
            }
        }
    }

    fun onTransactionRollback(isNewTransaction: Boolean = true) {
        val stack: ArrayDeque<TransactionScope> = transactionScopeStack.get()
        if (stack.isEmpty()) return
        val scope: TransactionScope = stack.removeLast()

        if (isNewTransaction) {
            scope.afterRollbackCallbacks.forEach { safeRun(it) }
        } else {
            parentOrCreate(stack).also {
                it.afterRollbackCallbacks.addAll(scope.afterRollbackCallbacks)
            }
            scope.afterCommitCallbacks.clear()
        }
    }

    fun clearIfStackEmpty() {
        val stack: ArrayDeque<TransactionScope> = transactionScopeStack.get()
        if (stack.isEmpty()) transactionScopeStack.remove()
    }

    private fun currentScope(): TransactionScope {
        val stack: ArrayDeque<TransactionScope> = transactionScopeStack.get()
        return if (stack.isEmpty()) {
            TransactionScope().also { stack.addLast(it) }
        } else {
            stack.last()
        }
    }

    private fun parentOrCreate(stack: ArrayDeque<TransactionScope>): TransactionScope =
        if (stack.isEmpty()) {
            TransactionScope().also { stack.addLast(it) }
        } else {
            stack.last()
        }

    private fun safeRun(function: () -> Unit) {
        try {
            function.invoke()
        } catch (exception: Exception) {
            Log.errorLogging(logger, exception) {
                AfterCommitCallbackException(
                    code = ErrorCode.FAILED_AFTER_COMMIT_CALLBACK,
                    message = exception.message
                )
            }
        }
    }
}
