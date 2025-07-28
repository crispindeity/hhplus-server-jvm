package kr.hhplus.be.server.queuetoken.application.service

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID
import kr.hhplus.be.server.config.jwt.JWTProperties
import org.springframework.stereotype.Component

@Component
class JWTHelper(
    private val jwtProperties: JWTProperties
) {
    fun createJWT(
        userId: UUID,
        queueNumber: Int
    ): String {
        val claimsSet: JWTClaimsSet =
            JWTClaimsSet
                .Builder()
                .subject(userId.toString())
                .issuer("api.entry.queue")
                .claim("userId", userId)
                .claim("queueNumber", queueNumber)
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(calculateExpiration()))
                .build()

        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
        signedJWT.sign(MACSigner(jwtProperties.secret))

        return signedJWT.serialize()
    }

    fun parseJWT(jwt: String): JWTClaimsSet {
        val signedJWT: SignedJWT = SignedJWT.parse(jwt)
        val verifier = MACVerifier(jwtProperties.secret)
        signedJWT.verify(verifier)
        return signedJWT.jwtClaimsSet
    }

    private fun calculateExpiration(): Instant =
        Instant.now().plus(jwtProperties.expirationMinutes, ChronoUnit.MINUTES)
}
