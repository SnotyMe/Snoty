package me.snoty.backend.authentication.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocalAuthLoginRequest(
	val email: String,
	val password: String,
)

@Serializable
data class LocalAuthLoginResponse(
	val accessToken: String,
	val refreshToken: String,
)
