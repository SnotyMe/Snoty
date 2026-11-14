package me.snoty.backend.adapter

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

interface AdapterSelector {
	fun <T : Adapter> select(adapterClass: KClass<T>, configKey: String): T?
	fun <T : Adapter> load(adapterClass: KClass<T>, configKey: String): T

	fun <T : Adapter> loadOptional(adapterClass: KClass<T>, configKey: String): T? =
		try {
			load(adapterClass, configKey)
		} catch (_: NoAdapterSelectedException) {
			logger.info { "No adapter selected for $configKey" }

			return null
		}
}
