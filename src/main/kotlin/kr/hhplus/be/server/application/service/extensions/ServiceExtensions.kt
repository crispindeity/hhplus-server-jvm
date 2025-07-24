package kr.hhplus.be.server.application.service.extensions

import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableSeatsResponse
import kr.hhplus.be.server.application.service.dto.AvailableSeatDto

internal fun AvailableSeatDto.toResponse(): FindAvailableSeatsResponse =
    FindAvailableSeatsResponse(
        id = this.id,
        number = this.number,
        price = this.price
    )
