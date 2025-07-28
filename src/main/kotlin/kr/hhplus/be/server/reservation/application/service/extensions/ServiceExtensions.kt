package kr.hhplus.be.server.reservation.application.service.extensions

internal inline fun <T> T?.orThrow(error: () -> Throwable): T = this ?: throw error()
