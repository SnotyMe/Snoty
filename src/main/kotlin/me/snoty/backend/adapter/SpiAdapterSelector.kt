package me.snoty.backend.adapter

import com.sksamuel.hoplite.ConfigFailure
import com.sksamuel.hoplite.fp.getOrElse
import io.github.oshai.kotlinlogging.KotlinLogging
import me.snoty.backend.config.ConfigException
import me.snoty.backend.config.ConfigLoader
import me.snoty.backend.config.loadConfig
import org.koin.core.Koin
import org.koin.core.annotation.Single
import org.koin.dsl.module
import org.koin.plugin.module.dsl.bind
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

data class AdapterConfig(val adapter: String)

@Single
class SpiAdapterSelector(
	private val configLoader: ConfigLoader,
	private val koin: Koin,
) : AdapterSelector {
	private val logger = KotlinLogging.logger {}

	private fun <T : Adapter> selectPotential(adapterClass: KClass<T>, configKey: String): List<T> {
		val providers = ServiceLoader.load(adapterClass.java).toList()

		val adapterConfig = configLoader.loadConfig<AdapterConfig>(configKey) {
			providers.forEach {
				it.autoconfigure(this)
				it.registerAlwaysOn(Adapter.OnRegisterAlwaysOn(
					koin = koin,
				))
			}
		}.getOrElse {
			when (it) {
				is ConfigFailure.MissingConfigValue if it.type.jvmErasure == AdapterConfig::class ->
					throw NoAdapterSelectedException(configKey)

				is ConfigFailure.DataClassFieldErrors if isAdapterUnconfigured(it) ->
					throw NoAdapterSelectedException(configKey)

				else -> throw ConfigException(it)
			}
		}

		val potential = providers.filter {
			adapterConfig.adapter in it.supportedTypes
		}
		logger.info { "Adapter selection for $configKey: requested=${adapterConfig.adapter} potential=${potential.map { it::class.simpleName }}" }

		return potential
	}

	override fun <T : Adapter> select(adapterClass: KClass<T>, configKey: String): T? =
		try {
			selectPotential(adapterClass, configKey).singleOrNull()
		} catch (_: NoAdapterSelectedException) {
			logger.info { "No adapter selected for $configKey" }

			return null
		}

	override fun <T : Adapter> load(adapterClass: KClass<T>, configKey: String): T {
		val potential = selectPotential(adapterClass, configKey)
		val selected = potential.singleOrNull()
		when {
			selected != null -> {
				val adapterModule = module {
					single<Adapter> { selected }.bind(adapterClass)
				}
				koin.loadModules(listOf(selected.koinModule, adapterModule))
				selected.onLoad(Adapter.OnLoad(
					koin = koin,
				))
				return selected
			}
			potential.isEmpty() -> throw NoAdapterSelectedException(configKey)
			else -> throw MultipleAdaptersSelectedException(configKey, potential)
		}
	}

	/**
	 * @return true if the exception was caused by the adapter config being unconfigured (i.e. missing the "adapter" field)
	 */
	private fun isAdapterUnconfigured(fail: ConfigFailure): Boolean {
		val fail = fail as? ConfigFailure.DataClassFieldErrors ?: return false
		if (fail.type.jvmErasure != AdapterConfig::class) return false

		val error = fail.errors.list.singleOrNull() ?: return false
		if (error !is ConfigFailure.ParamFailure) return false

		if (error.param.name != AdapterConfig::adapter.name || error.error !is ConfigFailure.MissingConfigValue) return false

		return true
	}
}
