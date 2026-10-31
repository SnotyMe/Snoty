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
				is ConfigFailure.MissingConfigValue if it.type.jvmErasure == AdapterConfig::class -> {
					logger.info { "No $configKey adapter selected!" }
					return@getOrElse null
				}

				else -> throw ConfigException(it)
			}
		} ?: return emptyList()

		val potential = providers.filter {
			adapterConfig.adapter in it.supportedTypes
		}
		logger.info { "Adapter selection for $configKey: requested=${adapterConfig.adapter} potential=${potential.map { it::class.simpleName }}" }

		return potential
	}

	override fun <T : Adapter> select(adapterClass: KClass<T>, configKey: String): T? =
		selectPotential(adapterClass, configKey).singleOrNull()

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
			potential.isEmpty() -> error("No $configKey adapter selected!")
			else -> error("Multiple $configKey adapters selected! ($potential)")
		}
	}

	override fun <T : Adapter> loadOptional(adapterClass: KClass<T>, configKey: String): T? = try {
		load(adapterClass, configKey)
	} catch (e: ConfigException) {
		val fail = e.fail as? ConfigFailure.DataClassFieldErrors ?: throw e

		val error = fail.errors.list.singleOrNull() ?: throw e

		if (error !is ConfigFailure.ParamFailure) throw e

		if (error.param.name != AdapterConfig::adapter.name || error.error !is ConfigFailure.MissingConfigValue) throw e

		null
	}
}
