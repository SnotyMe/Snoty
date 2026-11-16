package me.snoty.backend.authentication.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocalAuthRegisterRequest(
	val displayName: String,
	val email: String,
	val password: String,
)
