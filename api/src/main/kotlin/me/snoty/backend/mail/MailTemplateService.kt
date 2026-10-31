package me.snoty.backend.mail

import java.io.InputStream

interface MailTemplateService {
	fun sendMail(to: String, subject: String, bodyTemplate: InputStream, variables: Map<String, Any>)
}
