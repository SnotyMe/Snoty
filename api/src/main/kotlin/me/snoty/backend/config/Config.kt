package me.snoty.backend.config

data class Config(
	val server: ServerConfig = ServerConfig(),
	val port: Short = 8080,
	val monitoringPort: Short = 9000,
	val applicationMode: ApplicationMode,
	val publicHost: String,
	val frontendUrl: String =
		if (applicationMode.withFrontend) "${publicHost}/"
		else error("frontendUrl must be configured when running in Backend mode"),
	val corsHosts: List<String> = emptyList(),
	val environment: Environment,
	val antiAbuse: AntiAbuseConfig? = null,
)
