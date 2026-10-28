package me.snoty.backend.wiring.node

import me.snoty.backend.schema.NoSchema
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import kotlin.reflect.KClass

annotation class RegisterNode(
	val displayName: String,
	val icon: Icon = Icon(),
	/**
	 * Globally unique node type, in snake_case.
	 * Must only exist once instance-wide or side effects will occur.
	 */
	val name: String,
	val stereotype: NodeStereotype,
	val settingsType: KClass<out NodeSettings>,
	val inputType: KClass<out Any> = NoSchema::class,
	val outputType: KClass<out Any> = NoSchema::class,
)

annotation class Icon(
	val name: String = "",
	val color: String = "",
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SkipRegisterIfMissing(
	val value: Array<KClass<*>>
)
