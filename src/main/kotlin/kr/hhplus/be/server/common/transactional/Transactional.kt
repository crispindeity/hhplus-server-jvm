package kr.hhplus.be.server.common.transactional

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.DefaultTransactionDefinition

interface Runner {
    fun <T> run(
        function: () -> T?,
        readOnly: Boolean = false,
        propagation: Propagation
    ): T?

    fun <T> readOnly(
        function: () -> T?,
        propagation: Propagation
    ): T? = run(function, readOnly = true, propagation = propagation)
}

@Component
class Transactional(
    private val advice: Runner
) {
    fun <T : Any> run(
        propagation: Propagation = Propagation.REQUIRED,
        function: () -> T
    ): T = advice.run({ function() }, propagation = propagation)!!

    fun <T : Any> readOnly(
        propagation: Propagation = Propagation.REQUIRED,
        function: () -> T
    ): T = advice.readOnly({ function() }, propagation = propagation)!!

    @Component
    private class Advice(
        private val transactionManager: PlatformTransactionManager
    ) : Runner {
        override fun <T> run(
            function: () -> T?,
            readOnly: Boolean,
            propagation: Propagation
        ): T? {
            val definition: DefaultTransactionDefinition =
                DefaultTransactionDefinition().apply {
                    this.isReadOnly = readOnly
                    this.propagationBehavior = propagation.value()
                }

            val status: TransactionStatus = transactionManager.getTransaction(definition)
            return try {
                val result: T? = function()
                transactionManager.commit(status)
                result
            } catch (exception: Throwable) {
                transactionManager.rollback(status)
                throw exception
            }
        }
    }
}
