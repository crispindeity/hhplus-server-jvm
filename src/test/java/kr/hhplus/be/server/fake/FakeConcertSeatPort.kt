package kr.hhplus.be.server.fake

import kr.hhplus.be.server.concert.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.seat.domain.Seat

internal class FakeConcertSeatPort(
    private val seatPort: FakeSeatPort
) : ConcertSeatPort {
    private val storage = mutableMapOf<Long, ConcertSeat>()

    override fun getAvailableSeats(scheduleId: Long): List<AvailableSeatDto> {
        val concertSeats: List<ConcertSeat> =
            storage.values
                .filter {
                    it.scheduleId == scheduleId &&
                        it.status == ConcertSeat.SeatStatus.AVAILABLE
                }

        val seatIds: List<Long> = concertSeats.map { it.seatId }
        val seats: Map<Long, Seat> = seatPort.getAllSeat(seatIds).associateBy { it.id }

        return concertSeats.mapNotNull { concertSeat ->
            seats[concertSeat.seatId]?.let { seat ->
                AvailableSeatDto(
                    id = concertSeat.id,
                    number = seat.number,
                    price = seat.price,
                    status = concertSeat.status
                )
            }
        }
    }

    override fun getConcertSeat(concertSeatId: Long): ConcertSeat? = storage[concertSeatId]

    override fun update(concertSeat: ConcertSeat) {
        storage[concertSeat.id] = concertSeat
    }

    fun saveSingleSeat(
        id: Long,
        status: ConcertSeat.SeatStatus = ConcertSeat.SeatStatus.AVAILABLE
    ) {
        storage[id] =
            ConcertSeat(
                id = id,
                scheduleId = id,
                seatId = id,
                status = status
            )
    }
}
