package me.snoty.backend.authentication.local

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import me.snoty.backend.authentication.AuthenticationProvider
import me.snoty.backend.authentication.Role
import me.snoty.backend.authentication.User
import me.snoty.backend.utils.UnauthorizedException
import me.snoty.backend.utils.parseAuthHeader
import me.snoty.backend.utils.respondStatus
import me.snoty.core.user.UserId
import org.koin.core.annotation.Single

@Single
class LocalAuthenticationProvider(
	private val localAuthPersistenceService: LocalAuthPersistenceService,
	private val jwtService: JwtService,
) : AuthenticationProvider {
	override suspend fun getUserById(userId: UserId): User? =
		localAuthPersistenceService.getUserById(userId)?.toUser()

	override suspend fun getRolesByToken(token: String): List<Role> {
		val jwt = jwtService.verify(token, JwtService.CLAIM_TYPE_ACCESS)

		return getRolesById(UserId(jwt.subject))
	}

	override suspend fun getRolesById(userId: UserId): List<Role> =
		localAuthPersistenceService.getRolesByUserId(userId)

	override fun configureKtor(application: Application): Unit = with(application) {
		install(Authentication) {
			jwt("jwt-auth") {
				authHeader {  call ->
					call.request.parseAuthHeader()
				}

				verifier(jwtService.verifier)

				challenge { _, _ ->
					call.respondStatus(UnauthorizedException("JWT is invalid"))
				}

				validate { credential ->
					JWTPrincipal(credential.payload)
				}
			}
		}
	}
}
