package kr.hhplus.be.server.pointwallet.application.service

import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import kr.hhplus.be.server.pointtransaction.domain.PointTransaction
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.pointwallet.domain.PointWallet
import kr.hhplus.be.server.pointwallet.exception.PointWalletException
import kr.hhplus.be.server.user.application.port.UserPort
import kr.hhplus.be.server.user.exception.UserException
import org.slf4j.Logger
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service

@Service
internal class PointWalletService(
    private val userPort: UserPort,
    private val pointWalletPort: PointWalletPort,
    private val pointTransactionPort: PointTransactionPort,
    private val transactional: Transactional
) {
    private val logger: Logger = Log.getLogger(PointWalletService::class.java)

    companion object {
        const val MAX_RETRIES = 10
        const val BACKOFF_MILLIS = 50L
    }

    fun chargePoint(
        userId: UUID,
        amount: Long
    ): Long =
        Log.logging(logger) { log ->
            log["method"] = "chargePoint()"
            verifyUser(userId)

            repeat(MAX_RETRIES) { attempt ->
                try {
                    val chargedWallet: PointWallet =
                        transactional.run {
                            val foundWallet: PointWallet =
                                pointWalletPort.getWallet(userId)
                                    ?: throw PointWalletException(
                                        ErrorCode.NOT_FOUND_USER_POINT_WALLET
                                    )

                            val charged: PointWallet = foundWallet.chargePoint(amount)

                            pointWalletPort.update(charged)

                            pointTransactionPort.save(
                                PointTransaction(
                                    pointWalletId = charged.id,
                                    amount = amount,
                                    type = PointTransaction.Type.CHARGED
                                )
                            )
                            charged
                        }
                    return@logging chargedWallet.balance
                } catch (_: ObjectOptimisticLockingFailureException) {
                    log["retry_attempt"] = attempt + 1
                    Thread.sleep(BACKOFF_MILLIS)
                }
            }
            throw PointWalletException(ErrorCode.FAILED_RETRY, "chargePoint() retry fail")
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
