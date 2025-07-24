package kr.hhplus.be.server.application.extensions

internal inline fun <T> T?.orThrow(error: () -> Throwable): T = this ?: throw error()
