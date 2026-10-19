package me.snoty.backend.test

import me.snoty.backend.wiring.data.*
import me.snoty.backend.wiring.node.NodeHandleContext
import me.snoty.backend.wiring.node.NodeHandler
import me.snoty.core.node.NodeId
import me.snoty.core.node.NodeWithSettings

const val TYPE_MAP = "map"
const val TYPE_QUOTE = "quote"
const val TYPE_EXCEPTION = "exception"
const val TYPE_PROVIDES_NONEMPTY = "provides_nonempty"
const val TYPE_PROVIDES_EMPTY = "provides_empty"

abstract class TestNodeHandler : NodeHandler

object NoOpNodeHandler : TestNodeHandler() {
	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput =
		iterableStructOutput(
			input.map { it.value }
		)
}

/**
 * A handler that quotes the input using single quotes.
 * `test` -> `'test'`
 */
object QuoteHandler : TestNodeHandler() {
	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput) = mapInput<Any>(input) {
		simpleOutput("'${it}'")
	}
}

object ExceptionHandler : TestNodeHandler() {
	val exception = IllegalStateException("This is an exception")

	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput)
		= throw exception
}

open class GlobalMapHandler(
	private val map: MutableMap<NodeId, NodeInput> = mutableMapOf()
) : TestNodeHandler(), Map<NodeId, NodeInput> by map {
	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput) = processImpl(node, input)

	context(_: NodeHandleContext)
	fun processImpl(node: NodeWithSettings, input: NodeInput): NodeOutput {
		map[node.id] = input

		return mapInput<Any>(input) {
			simpleOutput(it)
		}
	}
}

class ProvidesNonEmptyHandler : GlobalMapHandler() {
	companion object {
		const val OUTPUT = "non-empty"
	}

	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		super.processImpl(node, input)
		return simpleOutput(OUTPUT)
	}
}

class ProvidesEmptyHandler : GlobalMapHandler() {
	context(_: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		super.processImpl(node, input)
		return iterableStructOutput<Unit>(emptyList())
	}
}
