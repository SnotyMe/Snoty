package me.snoty.backend.config

data class ServerConfig(
	val maxHeaderSize: Int = 4096 * 4,
	val openApi: OpenApiConfig = OpenApiConfig(),
)

data class OpenApiConfig(
	/**
	 * Dev: true
	 * Prod: false
	 */
	val enabled: Boolean? = null,
	/**
	 * Dev: false
	 * Prod: true
	 */
	val authorized: Boolean? = null,
)
