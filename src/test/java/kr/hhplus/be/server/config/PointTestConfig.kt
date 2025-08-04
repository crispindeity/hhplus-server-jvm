package kr.hhplus.be.server.config

import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.pointwallet.application.service.PointWalletService
import kr.hhplus.be.server.user.application.port.UserPort
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class PointTestConfig {
    @Bean
    fun userPort(): UserPort = mock(UserPort::class.java)

    @Bean
    fun pointWalletPort(): PointWalletPort = mock(PointWalletPort::class.java)

    @Bean
    fun pointTransactionPort(): PointTransactionPort = mock(PointTransactionPort::class.java)

    @Bean
    fun userPointService(
        userPort: UserPort,
        pointWalletPort: PointWalletPort,
        pointTransactionPort: PointTransactionPort,
        transactional: Transactional
    ): PointWalletService =
        PointWalletService(
            userPort,
            pointWalletPort,
            pointTransactionPort,
            transactional
        )
}
