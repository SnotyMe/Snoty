package me.snoty.backend.wiring.node.metadata

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import me.snoty.backend.utils.DurationSerializer
import kotlin.time.Duration

val metadataSerializersModule = SerializersModule {
	contextual(Duration::class, DurationSerializer)
}

val metadataJson = Json {
	serializersModule += metadataSerializersModule

	ignoreUnknownKeys = true
	encodeDefaults = true
	allowStructuredMapKeys = true
	allowSpecialFloatingPointValues = true
	coerceInputValues = true
	explicitNulls = true
	allowTrailingComma = true
	prettyPrint = true
}
