package me.snoty.backend.authentication.local

import kotlinx.serialization.Serializable
import me.snoty.backend.authentication.AuthenticationMetadata

@Serializable
data class LocalAuthenticationMetadata(
	val selfRegistration: LocalSelfRegistrationMetadata?,
) : AuthenticationMetadata {
	override val adapter: String = LOCALAUTH_ADAPTER_TYPE
}

@Serializable
data class LocalSelfRegistrationMetadata(
	val enabled: Boolean,
	val emailRegex: String,
)
