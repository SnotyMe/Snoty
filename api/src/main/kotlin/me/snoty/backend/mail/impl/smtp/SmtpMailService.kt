package me.snoty.backend.mail.impl.smtp

import jakarta.mail.Session
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import me.snoty.backend.mail.MailSendRequest
import me.snoty.backend.mail.MailService

private fun createMessage(mail: MailSendRequest, to: String, mimeType: String, session: Session): MimeMessage {
	val mimeBodyPart = MimeBodyPart()
	mimeBodyPart.setContent(mail.body, mimeType)

	val multipart = MimeMultipart()
	multipart.addBodyPart(mimeBodyPart)

	return MimeMessage(session).apply {
		setSubject(mail.subject)
		setRecipients(MimeMessage.RecipientType.TO, to)
		setContent(multipart)
	}
}

class SmtpMailService(private val config: SmtpConfig) : MailService {
	val properties = config.toConfiguration()

	override fun send(mails: Collection<MailSendRequest>, to: String, mimeType: String) {
		val session = Session.getInstance(properties)
		session.getTransport("smtp").use { transport ->
			transport.connect(config.host, config.username, config.password)

			mails.map { mail ->
				createMessage(mail, to, mimeType, session)
			}.forEach { message ->
				transport.sendMessage(message, message.allRecipients)
			}
		}
	}
}
