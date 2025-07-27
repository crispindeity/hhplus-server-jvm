package kr.hhplus.be.server.pointtransaction.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "point_transactions")
internal class PointTransactionEntity(
    @Column(nullable = false)
    val pointWalletId: Long,
    @Column(nullable = false)
    val amount: Long,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: Type
) : BaseEntity() {
    enum class Type {
        CHARGED,
        USED
    }
}
