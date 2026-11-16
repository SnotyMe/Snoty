package me.snoty.backend.authentication.local

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import me.snoty.backend.utils.BadRequestException
import me.snoty.core.user.UserId
import org.koin.core.annotation.Single
import java.time.Instant
import kotlin.time.Clock
import kotlin.time.toJavaInstant

@Single
class JwtService(
	private val config: LocalAuthConfig,
) {
	companion object {
		const val ISSUER = "snoty"
		const val CLAIM_TYPE = "typ"
		const val CLAIM_TYPE_REFRESH = "Refresh"
		const val CLAIM_TYPE_ACCESS = "Access"
	}

	private val algorithm = Algorithm.HMAC256(config.jwtSecret)
	val verifier: JWTVerifier = JWT.require(algorithm)
		.withIssuer(ISSUER)
		.build()

	fun createAccessToken(user: LocalUser): String = JWT.create()
		.withIssuedAt(Instant.now())
		.withExpiresAt(Clock.System.now().plus(config.accessTokenExpiration).toJavaInstant())
		.withIssuer(ISSUER)
		.withSubject(user.id.value)
		.withClaim(CLAIM_TYPE, CLAIM_TYPE_ACCESS)
		.withClaim("email", user.email)
		.withClaim("displayName", user.displayName)
		.sign(algorithm)

	fun createRefreshToken(user: LocalUser): String = JWT.create()
		.withIssuedAt(Instant.now())
		.withExpiresAt(Clock.System.now().plus(config.refreshTokenExpiration).toJavaInstant())
		.withIssuer(ISSUER)
		.withSubject(user.id.value)
		.withClaim(CLAIM_TYPE, CLAIM_TYPE_REFRESH)
		.sign(algorithm)

	fun verify(token: String, type: String): DecodedJWT {
		val token = try {
			verifier.verify(token)
		} catch (_: Exception) {
			throw BadRequestException("Invalid token")
		}

		if (token.getClaim(CLAIM_TYPE).asString() != type) {
			throw BadRequestException("Invalid token type")
		}

		return token
	}

	fun getUserIdFromRefreshToken(token: String): UserId {
		val jwt = verify(token, CLAIM_TYPE_REFRESH)
		return UserId(jwt.subject)
	}
}
