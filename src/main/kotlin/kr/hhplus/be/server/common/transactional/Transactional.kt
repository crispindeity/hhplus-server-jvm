package kr.hhplus.be.server.common.transactional

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.DefaultTransactionDefinition

interface Runner {
    fun <T> run(
        function: () -> T?,
        readOnly: Boolean = false,
        isolation: Isolation,
        propagation: Propagation
    ): T?

    fun <T> readOnly(
        function: () -> T?,
        isolation: Isolation,
        propagation: Propagation
    ): T? = run(function, readOnly = true, isolation = isolation, propagation = propagation)
}

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
        private val transactionManager: PlatformTransactionManager
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
            return try {
                val result: T? = function()
                transactionManager.commit(status)
                result
            } catch (exception: Throwable) {
                if (!status.isCompleted) {
                    transactionManager.rollback(status)
                }
                throw exception
            }
        }
    }
}
