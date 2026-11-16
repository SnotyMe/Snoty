package me.snoty.backend.authentication.local

import io.ktor.openapi.*
import me.snoty.backend.authentication.AuthenticationAdapter
import me.snoty.backend.config.ConfigLoader
import me.snoty.backend.config.load
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.time.Duration

const val LOCALAUTH_ADAPTER_TYPE = "local"
private const val CONFIG_KEY = "${AuthenticationAdapter.CONFIG_GROUP}.${LOCALAUTH_ADAPTER_TYPE}"

@Module
@ComponentScan
object LocalAuthKoinModule

@Single
class LocalAuthAdapter : AuthenticationAdapter {
	override val supportedTypes = listOf(LOCALAUTH_ADAPTER_TYPE)

	override val koinModule = LocalAuthKoinModule.module()

	override suspend fun buildAuthenticationMetadata(event: AuthenticationAdapter.OnBuildAuthenticationMetadata): LocalAuthenticationMetadata {
		val config: LocalAuthConfig = event.koin.get()
		return LocalAuthenticationMetadata(
			selfRegistration = when {
				!config.selfRegistration.enabled -> null
				else -> LocalSelfRegistrationMetadata(
					enabled = config.selfRegistration.enabled,
					emailRegex = config.selfRegistration.emailRegex,
				)
			}
		)
	}

	override fun getMetadataJsonSchema(jsonSchemaInference: JsonSchemaInference): JsonSchema =
		jsonSchemaInference.jsonSchema<LocalAuthenticationMetadata>()
}

data class LocalAuthConfig(
	val jwtSecret: String,
	val accessTokenExpiration: Duration = Duration.parse("P1D"),
	val refreshTokenExpiration: Duration = Duration.parse("P7D"),
	val selfRegistration: LocalAuthSelfRegistrationConfig = LocalAuthSelfRegistrationConfig(),
)

data class LocalAuthSelfRegistrationConfig(
	val enabled: Boolean = true,
	val emailRegex: String = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
)

@Single
fun provideLocalAuthConfig(configLoader: ConfigLoader): LocalAuthConfig =
	configLoader.load(CONFIG_KEY)
