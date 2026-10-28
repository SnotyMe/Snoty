package me.snoty.backend.mail.impl.smtp

import me.snoty.backend.config.toProperties

data class SmtpConfig(
	val host: String,
	val port: Int,
	val startTls: Boolean = false,
	val username: String?,
	val password: String?,
	val from: String,
)

fun SmtpConfig.toConfiguration() = mapOf(
	"mail.smtp.auth" to (username != null || password != null),
	"mail.smtp.starttls.enable" to startTls,
	"mail.smtp.host" to host,
	"mail.smtp.port" to port,
	"mail.smtp.user" to username,
	"mail.smtp.password" to password,
	"mail.smtp.from" to from,
)
	.filterValues { it != null }
	.toProperties()
