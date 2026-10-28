package me.snoty.backend.mail

data class MailSendRequest(
	val subject: String,
	val body: String,
)
