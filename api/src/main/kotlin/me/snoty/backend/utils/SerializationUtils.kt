package me.snoty.backend.utils

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import me.snoty.backend.wiring.node.EmptyNodeSettings
import me.snoty.backend.wiring.node.InvalidNodeSettings
import me.snoty.backend.wiring.node.NodeSettings
import me.snoty.backend.wiring.node.metadata.metadataSerializersModule
import org.koin.core.annotation.Single
import kotlin.reflect.KClass
import kotlin.time.Duration

object DurationSerializer : KSerializer<Duration> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("snoty.time.Duration", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: Duration) =
		encoder.encodeString(value.toProperIsoString())

	override fun deserialize(decoder: Decoder): Duration =
		Duration.parse(decoder.decodeString())
}

/**
 * A hacky way to encode an object to a JSON string using the serializer of its class.
 * This allows users to serialize objects whose classes are not known at compile time.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
fun <T : Any> Json.hackyEncodeToString(it: T) = encodeToString((it::class as KClass<T>).serializer(), it)

val kotlinxSerializersModule = SerializersModule {
	polymorphic(NodeSettings::class) {
		subclass(EmptyNodeSettings::class)
		subclass(InvalidNodeSettings::class)
	}
}

@Single
fun provideSerializersModule(): SerializersModule = kotlinxSerializersModule + metadataSerializersModule
