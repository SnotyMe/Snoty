package me.snoty.node.mail

import me.snoty.backend.mail.MailSendRequest
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.get
import me.snoty.backend.wiring.node.*
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import me.snoty.core.node.getConfig
import org.koin.core.annotation.Single

@Single
@RegisterNode(
	name = "mail",
	displayName = "E-Mail",
	icon = Icon(name = "lucide-mail"),
	stereotype = NodeStereotype.END,
	inputType = MailInput::class,
	settingsType = MailSettings::class,
)
@SkipRegisterIfMissing([MailNodeService::class])
class MailNodeHandler(private val mailService: MailNodeService) : NodeHandler {
	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		val settings: MailSettings = node.getConfig()

		val mails: List<MailSendRequest> = input.map {
			it.get()
		}

		mailService.send(mails = mails, to = settings.to, mimeType = settings.mimeType)

		return emptyList()
	}
}
