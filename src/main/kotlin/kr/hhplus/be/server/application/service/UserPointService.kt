package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.application.port.PointWalletPort
import kr.hhplus.be.server.application.port.UserPort
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.PointWalletException
import kr.hhplus.be.server.common.exception.UserException
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.domain.PointWallet
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
internal class UserPointService(
    private val userPort: UserPort,
    private val pointWalletPort: PointWalletPort
) {
    private val logger: Logger = Log.getLogger(UserPointService::class.java)

    @Transactional
    fun chargePoint(
        userId: UUID,
        amount: Long
    ): Long =
        Log.logging(logger) { log ->
            log["method"] = "chargePoint()"
            verifyUser(userId)
            val foundWallet: PointWallet =
                pointWalletPort.getWallet(userId) ?: throw PointWalletException(
                    ErrorCode.NOT_FOUND_USER_POINT_WALLET
                )

            val chargedWallet: PointWallet = foundWallet.chargePoint(amount)

            pointWalletPort.update(chargedWallet)
            chargedWallet.balance
        }

    fun getPoint(userId: UUID): Long =
        Log.logging(logger) { log ->
            log["method"] = "getPoint()"
            verifyUser(userId)
            val foundWallet: PointWallet =
                pointWalletPort.getWallet(userId) ?: throw PointWalletException(
                    ErrorCode.NOT_FOUND_USER_POINT_WALLET
                )
            foundWallet.balance
        }

    private fun verifyUser(userId: UUID) {
        if (!userPort.exists(userId)) {
            UserException(ErrorCode.NOT_FOUND_USER)
        }
    }
}
