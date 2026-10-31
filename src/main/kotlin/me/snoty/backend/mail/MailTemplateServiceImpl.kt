package me.snoty.backend.mail

import liqp.TemplateParser
import org.koin.core.annotation.Single

@Single
class MailTemplateServiceImpl(
	private val mailService: MailService,
) : MailTemplateService {
	private val templateParser = TemplateParser.DEFAULT

	override fun sendMail(
		to: String,
		subject: String,
		bodyTemplate: String,
		variables: Map<String, Any>
	) {
		val body = templateParser.parse(bodyTemplate)
			.render(variables)

		mailService.send(
			mails = listOf(
				MailSendRequest(
					subject = subject,
					body = body,
				)
			),
			to = to,
			mimeType = TEXT_HTML_UTF8,
		)
	}
}
