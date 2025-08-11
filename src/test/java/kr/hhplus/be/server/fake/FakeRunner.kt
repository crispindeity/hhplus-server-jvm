package kr.hhplus.be.server.fake

import kr.hhplus.be.server.common.transactional.Runner
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation

class FakeRunner : Runner {
    override fun <T> run(
        function: () -> T?,
        readOnly: Boolean,
        isolation: Isolation,
        propagation: Propagation
    ): T? = function()
}
