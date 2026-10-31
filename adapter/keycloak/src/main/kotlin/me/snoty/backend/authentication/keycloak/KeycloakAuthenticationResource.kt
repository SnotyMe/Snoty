package me.snoty.backend.authentication.keycloak

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import kotlinx.serialization.Serializable
import me.snoty.backend.authentication.oidc.OidcConfig
import me.snoty.backend.authentication.oidc.oidcAuthenticationResource
import me.snoty.backend.config.Config
import me.snoty.backend.mail.MailTemplateService
import me.snoty.backend.server.routing.Resource
import me.snoty.backend.utils.InternalServerErrorException
import me.snoty.backend.utils.NotImplementedException
import me.snoty.backend.utils.http.INTERNAL_HTTP_CLIENT
import me.snoty.backend.utils.respondStatus
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.UserRepresentation
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.temporal.ChronoUnit

@Single
@Named("keycloakOidcAuthentication")
fun keycloakOidcAuthenticationResource(
    authConfig: OidcConfig,
    @Named(INTERNAL_HTTP_CLIENT) httpClient: HttpClient,
    serverSettings: OAuthServerSettings.OAuth2ServerSettings,
) =
    oidcAuthenticationResource(authConfig, httpClient, serverSettings)

@Serializable
data class KeycloakRegisterRequest(
    val email: String,
    val displayName: String,
)

@Single
@Named("keycloakAuthentication")
fun keycloakAuthenticationResource(
    config: Config,
    keycloakConfig: KeycloakConfig,
    realmResource: RealmResource,
    mailTemplateService: MailTemplateService,
) = Resource {
    val logger = KotlinLogging.logger {}

    val onboardingMailTemplate = javaClass
        .getResourceAsStream("/mail/onboarding.html") ?: error("Couldn't load Onboarding Mail template")

    route("/auth/keycloak") {
        post("/register") {
            val selfRegistrationConfig = keycloakConfig.selfRegistration
            if (selfRegistrationConfig?.enabled != true) {
				return@post call.respondStatus(NotImplementedException("Self-registration is disabled"))
			}

            val request: KeycloakRegisterRequest = call.receive()

            val user = UserRepresentation().apply {
                isEnabled = false

                username = request.email
                email = request.email
                isEmailVerified = false
                attributes = mapOf(
                    "displayName" to listOf(request.displayName),
                    "onboarded" to listOf("false"),
                )
            }

            val response = realmResource.users().create(user)
			when {
				response.status == HttpStatusCode.Created.value -> {
                    val locationHeader = response.headers["Location"]
                        ?.singleOrNull()

                    if (locationHeader == null) {
                        logger.error { "User created but no Location header returned" }
                        return@post call.respondStatus(InternalServerErrorException("Unknown error while creating user"))
                    }

                    val userId = (locationHeader as String).substringAfterLast("/")

                    logger.info { "User '${user.email}' created with ID: $userId" }

                    val jwt = JWT.create()
                        .withSubject(userId)
                        .withClaim("clientId", keycloakConfig.clientId)
                        .withClaim("email", user.email)
                        .withClaim("displayName", request.displayName)
                        .withExpiresAt(Instant.now().plus(12, ChronoUnit.HOURS))
                        .sign(Algorithm.HMAC256(keycloakConfig.clientSecret))

                    mailTemplateService.sendMail(
                        to = user.email,
                        subject = "Welcome to Snoty!",
                        bodyTemplate = onboardingMailTemplate,
                        variables = mapOf(
                            "displayName" to request.displayName,
                            "email" to request.email,
                            "onboardingUrl" to "${config.frontendUrl.removeSuffix("/")}/auth/onboarding?token=$jwt",
                        )
                    )

                    return@post call.respond(HttpStatusCode.Created, userId)
                }
                else -> {
                    logger.error { "Failed to create user: ${response.status} - ${response.readEntity(String::class.java)}" }
					return@post call.respondStatus(InternalServerErrorException("Failed to create user"))
				}
			}
        }.describe {
            requestBody {
                schema = jsonSchema<KeycloakRegisterRequest>()
            }
        }
    }
}
