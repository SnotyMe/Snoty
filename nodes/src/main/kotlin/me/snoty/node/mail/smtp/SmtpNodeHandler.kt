package me.snoty.node.mail.smtp

import me.snoty.backend.mail.MailSendRequest
import me.snoty.backend.mail.impl.smtp.SmtpMailService
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.get
import me.snoty.backend.wiring.node.Icon
import me.snoty.backend.wiring.node.NodeHandleContext
import me.snoty.backend.wiring.node.NodeHandler
import me.snoty.backend.wiring.node.RegisterNode
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import me.snoty.node.mail.MailInput
import org.koin.core.annotation.Single

@RegisterNode(
	name = "smtp",
	displayName = "SMTP",
	icon = Icon(name = "lucide-mail"),
	settingsType = SmtpSettings::class,
	inputType = MailInput::class,
	stereotype = NodeStereotype.END,
)
@Single
class SmtpNodeHandler : NodeHandler {
	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		val settings = node.settings as SmtpSettings
		val smtpConfig = settings.toConfiguration()
		val service = SmtpMailService(smtpConfig)

		val mails = input.map { it.get<MailSendRequest>() }

		service.send(mails = mails, to = settings.to, mimeType = settings.mimeType)

		return emptyList()
	}
}
