package me.snoty.backend.authentication.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocalAuthRefreshRequest(
	val refreshToken: String,
)
