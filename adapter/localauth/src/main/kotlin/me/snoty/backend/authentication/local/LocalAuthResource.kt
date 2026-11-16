package me.snoty.backend.authentication.local

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.describe
import me.snoty.backend.authentication.local.dto.LocalAuthLoginRequest
import me.snoty.backend.authentication.local.dto.LocalAuthRefreshRequest
import me.snoty.backend.authentication.local.dto.LocalAuthRegisterRequest
import me.snoty.backend.server.routing.Resource
import me.snoty.backend.utils.NotImplementedException
import me.snoty.backend.utils.respondStatus
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@Named("localAuth")
fun localAuthResource(
	localAuthConfig: LocalAuthConfig,
	service: LocalAuthService,
) = Resource {
	route("/auth/local") {
		post("/register") {
			if (!localAuthConfig.selfRegistration.enabled)
				return@post call.respondStatus(NotImplementedException("Self-registration is disabled"))

			val request: LocalAuthRegisterRequest = call.receive()

			val response = service.createUser(
				email = request.email,
				password = request.password,
				displayName = request.displayName,
			)

			return@post call.respond(response)
		}

		post("/login") {
			val request: LocalAuthLoginRequest = call.receive()

			val response = service.loginUser(
				email = request.email,
				password = request.password,
			)

			return@post call.respond(response)
		}

		post("/refresh") {
			val request: LocalAuthRefreshRequest = call.receive()

			val response = service.refreshToken(
				refreshToken = request.refreshToken,
			)

			return@post call.respond(response)
		}
	}.describe {
		tag("auth")
	}
}
