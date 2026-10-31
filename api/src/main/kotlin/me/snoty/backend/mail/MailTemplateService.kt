package me.snoty.backend.mail

interface MailTemplateService {
	fun sendMail(to: String, subject: String, bodyTemplate: String, variables: Map<String, Any>)
}
