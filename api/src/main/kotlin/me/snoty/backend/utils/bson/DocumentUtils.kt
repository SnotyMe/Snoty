package me.snoty.backend.utils.bson

import org.bson.Document
import org.bson.codecs.configuration.CodecRegistry

/**
 * Encodes all non-primitives in the [Document] using the provided [codecRegistry]
 */
fun Document.encodeObjects(codecRegistry: CodecRegistry) = Document(this.mapValues { (_, value) ->
	val packageName = value.javaClass.packageName
	if (packageName.startsWith("kotlin") || packageName.startsWith("java") || value::class == Document::class) value
	else codecRegistry.encode(value)
})
