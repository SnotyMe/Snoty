package me.snoty.backend.mail

interface MailService {
	fun send(mails: Collection<MailSendRequest>, to: String, mimeType: String)
}
