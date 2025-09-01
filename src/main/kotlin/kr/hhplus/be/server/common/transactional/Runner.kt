package kr.hhplus.be.server.common.transactional

import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation

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
