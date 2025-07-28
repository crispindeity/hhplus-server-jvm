package kr.hhplus.be.server.concertschedule.adapter.persistence.extensions

import kr.hhplus.be.server.concertschedule.adapter.persistence.entity.ConcertScheduleEntity
import kr.hhplus.be.server.concertschedule.domain.ConcertSchedule

internal fun ConcertScheduleEntity.toDomain(): ConcertSchedule =
    ConcertSchedule(
        id = this.id!!,
        concertId = this.concertId,
        date = this.date
    )
