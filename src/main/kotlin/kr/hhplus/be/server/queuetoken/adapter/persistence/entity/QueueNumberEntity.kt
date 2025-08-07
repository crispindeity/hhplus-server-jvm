package kr.hhplus.be.server.queuetoken.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "queue_numbers")
internal class QueueNumberEntity(
    @Id
    @Column(length = 20)
    val id: String = "entry_queue",
    @Column(nullable = false)
    val number: Int
) : BaseEntity()
