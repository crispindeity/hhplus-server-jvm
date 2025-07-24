package kr.hhplus.be.server.fake

import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.domain.ConcertSeat
import kr.hhplus.be.server.domain.Seat

internal class FakeConcertSeatPort(
    private val seatPort: FakeSeatPort
) : ConcertSeatPort {
    private val storage = mutableMapOf<Long, MutableList<ConcertSeat>>()

    override fun getAvailableSeats(scheduleId: Long): List<AvailableSeatDto> {
        val concertSeats: List<ConcertSeat> =
            storage[scheduleId]
                .orEmpty()
                .filter { it.status == ConcertSeat.SeatStatus.AVAILABLE }

        val seatIds: List<Long> = concertSeats.map { it.seatId }
        val seats: Map<Long, Seat> = seatPort.getAllSeat(seatIds).associateBy { it.id }

        return concertSeats.mapNotNull { concertSeat ->
            val seat: Seat? = seats[concertSeat.seatId]
            seat?.let {
                AvailableSeatDto(
                    id = seat.id,
                    number = seat.number,
                    price = seat.price,
                    status = concertSeat.status
                )
            }
        }
    }

    fun saveSingleSeat(
        id: Long,
        status: ConcertSeat.SeatStatus = ConcertSeat.SeatStatus.AVAILABLE
    ) {
        storage[id] =
            mutableListOf(
                ConcertSeat(
                    id = id,
                    scheduleId = id,
                    seatId = id,
                    status = status
                )
            )
    }
}
