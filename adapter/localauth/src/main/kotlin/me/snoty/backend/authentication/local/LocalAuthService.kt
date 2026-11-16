package me.snoty.backend.authentication.local

import me.snoty.backend.authentication.local.dto.LocalAuthLoginResponse
import me.snoty.backend.utils.BadRequestException
import me.snoty.backend.utils.ConflictException
import org.koin.core.annotation.Single
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.keygen.KeyGenerators

@Single
class LocalAuthService(
	private val persistenceService: LocalAuthPersistenceService,
	private val jwtService: JwtService,
	config: LocalAuthConfig,
) {
	private val encoder = Argon2PasswordEncoder(
		/* saltLength = */ 16,
		/* hashLength = */ 32,
		/* parallelism = */ 1,
		/* memory = */ 32 * 1024,
		/* iterations = */ 1,
	)
	private val dummyPasswordGenerator = KeyGenerators.secureRandom(16)
	private val emailRegex = config.selfRegistration.emailRegex.toRegex()

	suspend fun createUser(email: String, password: String, displayName: String): LocalAuthLoginResponse {
		if (!email.matches(emailRegex)) {
			throw BadRequestException("Invalid email address!")
		}

		if (persistenceService.getUserByEmail(email) != null) {
			throw ConflictException("User with this email already exists")
		}

		val passwordHash = encoder.encode(password)!!

		val user = persistenceService.createUser(
			email = email,
			passwordHash = passwordHash,
			displayName = displayName,
		)

		return createLoginResponse(user)
	}

	suspend fun loginUser(email: String, password: String): LocalAuthLoginResponse {
		val lookup = persistenceService.getUserWithPasswordHash(email)

		// matching is always executed to prevent timing attacks
		val passwordMatches = encoder.matches(
			password,
			lookup?.second ?: encoder.encode(dummyPasswordGenerator.generateKey().contentToString()),
		)

		if (!passwordMatches || lookup == null) {
			throw BadRequestException("Invalid email or password")
		}

		return createLoginResponse(lookup.first)
	}

	suspend fun refreshToken(refreshToken: String): LocalAuthLoginResponse {
		val userId = jwtService.getUserIdFromRefreshToken(refreshToken)
		val user = persistenceService.getUserById(userId)
			?: throw BadRequestException("Invalid refresh token")

		return createLoginResponse(user)
	}

	private fun createLoginResponse(user: LocalUser) = LocalAuthLoginResponse(
		accessToken = jwtService.createAccessToken(user),
		refreshToken = jwtService.createRefreshToken(user),
	)
}
