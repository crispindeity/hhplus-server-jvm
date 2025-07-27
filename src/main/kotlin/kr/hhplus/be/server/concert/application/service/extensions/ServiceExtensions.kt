package kr.hhplus.be.server.concert.application.service.extensions

import kr.hhplus.be.server.concert.adapter.web.response.FindAvailableSeatsResponse
import kr.hhplus.be.server.concert.application.service.dto.AvailableSeatDto

internal fun AvailableSeatDto.toResponse(): FindAvailableSeatsResponse =
    FindAvailableSeatsResponse(
        id = this.id,
        number = this.number,
        price = this.price
    )
