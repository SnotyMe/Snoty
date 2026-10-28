package me.snoty.node.mail

import kotlinx.serialization.Serializable
import me.snoty.backend.mail.TEXT_HTML_UTF8
import me.snoty.backend.schema.FieldDefaultValue
import me.snoty.backend.schema.FieldHidden
import me.snoty.backend.wiring.node.NodeSettings

@Serializable
data class MailSettings(
	val to: String,
	@FieldHidden
	@FieldDefaultValue(TEXT_HTML_UTF8)
	val mimeType: String = TEXT_HTML_UTF8,
) : NodeSettings
