package me.snoty.node.mail.smtp

import kotlinx.serialization.Serializable
import me.snoty.backend.mail.TEXT_HTML_UTF8
import me.snoty.backend.mail.impl.smtp.SmtpConfig
import me.snoty.backend.schema.FieldCensored
import me.snoty.backend.schema.FieldDefaultValue
import me.snoty.backend.schema.FieldHidden
import me.snoty.backend.schema.FieldName
import me.snoty.backend.wiring.node.NodeSettings

@Serializable
data class SmtpSettings(
	val host: String,
	@FieldDefaultValue("587")
	val port: Int,
	@FieldName("STARTTLS")
	val startTls: Boolean = false,
	val username: String,
	@FieldCensored
	val password: String,
	val from: String,
	val to: String,
	@FieldHidden
	@FieldDefaultValue(TEXT_HTML_UTF8)
	val mimeType: String = TEXT_HTML_UTF8,
) : NodeSettings

fun SmtpSettings.toConfiguration() = SmtpConfig(
	host = host,
	port = port,
	startTls = startTls,
	username = username,
	password = password,
	from = from,
)
