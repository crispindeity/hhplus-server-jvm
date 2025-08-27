package kr.hhplus.be.server.common.transactional

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class Transactional(
    private val advice: Runner
) {
    fun <T : Any> run(
        propagation: Propagation = Propagation.REQUIRED,
        isolation: Isolation = Isolation.REPEATABLE_READ,
        function: () -> T
    ): T = advice.run({ function() }, isolation = isolation, propagation = propagation)!!

    fun <T : Any> readOnly(
        propagation: Propagation = Propagation.REQUIRED,
        isolation: Isolation = Isolation.REPEATABLE_READ,
        function: () -> T
    ): T = advice.readOnly({ function() }, isolation = isolation, propagation = propagation)!!

    @Component
    private class Advice(
        private val transactionManager: PlatformTransactionManager,
        private val afterCommitExecutor: AfterCommitExecutor
    ) : Runner {
        override fun <T> run(
            function: () -> T?,
            readOnly: Boolean,
            isolation: Isolation,
            propagation: Propagation
        ): T? {
            val definition: DefaultTransactionDefinition =
                DefaultTransactionDefinition().apply {
                    this.isReadOnly = readOnly
                    this.propagationBehavior = propagation.value()
                    this.isolationLevel = isolation.value()
                }
            val status: TransactionStatus = transactionManager.getTransaction(definition)
            afterCommitExecutor.pushTransactionScope()

            return try {
                val result: T? = function()
                transactionManager.commit(status)
                afterCommitExecutor.onTransactionCommit(status.isNewTransaction)
                result
            } catch (exception: Throwable) {
                if (!status.isCompleted) {
                    transactionManager.rollback(status)
                }
                afterCommitExecutor.onTransactionRollback(status.isNewTransaction)
                throw exception
            } finally {
                afterCommitExecutor.clearIfStackEmpty()
            }
        }
    }
}
