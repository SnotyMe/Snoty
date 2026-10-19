package me.snoty.backend.wiring.data

import me.snoty.backend.wiring.node.NodeHandleContext
import me.snoty.backend.wiring.node.NodeSettings
import me.snoty.core.node.NodeWithSettings
import me.snoty.core.node.getConfig

context(ctx: NodeHandleContext)
inline fun <reified T : Any> each(input: NodeInput, block: (T) -> Unit): NodeOutput = input
	.forEach {
		block(it.get())
	}
	.let { emptyList() }

context(ctx: NodeHandleContext)
inline fun <reified T : Any, reified Settings : NodeSettings> eachWithSettings(
	input: NodeInput,
	node: NodeWithSettings,
	block: (T, Settings) -> Unit
): NodeOutput {
	if (input.isEmpty()) return emptyList()

	val settings = node.getConfig<Settings>()

	for (element in input) {
		val data = element.get<T>()
		block(data, settings)
	}

	return emptyList()
}

context(ctx: NodeHandleContext)
inline fun <reified T : Any> mapInput(input: NodeInput, block: (T) -> NodeOutput): NodeOutput =
	input.flatMap {
		block(it.get<T>())
	}

context(ctx: NodeHandleContext)
inline fun <reified T : Any, reified Settings : NodeSettings> mapInputWithSettings(
	input: NodeInput,
	node: NodeWithSettings,
	block: (T, Settings) -> NodeOutput
): NodeOutput {
	if (input.isEmpty()) return emptyList()

	val settings = node.getConfig<Settings>()

	return input.flatMap {
		val data = it.get<T>()
		block(data, settings)
	}
}
