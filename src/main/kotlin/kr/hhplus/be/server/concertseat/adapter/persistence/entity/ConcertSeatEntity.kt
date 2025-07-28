package kr.hhplus.be.server.concertseat.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "concert_seats")
internal class ConcertSeatEntity(
    /*
    fixme   :: baseEntity 에 Id 값을 넣는 바보 같은 실수를 했다. 일단 진행 후 추후 수정
     author :: heechoel shin
     date   :: 2025-07-25T1:36:19KST
     */
    override val id: Long? = null,
    @Column(nullable = false)
    val scheduleId: Long,
    @Column(nullable = false)
    val seatId: Long,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: Status
) : BaseEntity() {
    enum class Status {
        HELD,
        AVAILABLE,
        RESERVED
    }
}
