package me.snoty.node.schedule

import kotlinx.serialization.Serializable
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.node.*
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import org.koin.core.annotation.Single

@Serializable
class ManualTriggerNodeSettings : NodeSettings

@RegisterNode(
	name = "manual_trigger",
	displayName = "Manual Trigger",
	icon = Icon(name = "lucide-play"),
	stereotype = NodeStereotype.TRIGGER,
	settingsType = ManualTriggerNodeSettings::class,
	outputType = Unit::class,
)
@Single
class ManualTriggerNodeHandler : NodeHandler {
	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput = emptyList()
}
