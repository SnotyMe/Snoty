package me.snoty.backend.scheduling.jobrunr

import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import me.snoty.backend.wiring.data.INTERMEDIATE_DATA_SERIALIZERS_MODULE
import org.jobrunr.configuration.JobRunr
import org.jobrunr.configuration.JobRunrConfiguration
import org.jobrunr.configuration.JobRunrMicroMeterIntegration
import org.jobrunr.dashboard.JobRunrDashboardWebServerConfiguration
import org.jobrunr.kotlin.utils.mapper.KotlinxSerializationJsonMapper
import org.jobrunr.scheduling.JobRequestScheduler
import org.jobrunr.server.BackgroundJobServer
import org.jobrunr.server.BackgroundJobServerConfiguration
import org.jobrunr.server.JobActivator
import org.jobrunr.storage.StorageProvider
import org.koin.core.Koin
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun provideJobRunr(
	meterRegistry: MeterRegistry,
	storageProvider: StorageProvider,
	@Named(INTERMEDIATE_DATA_SERIALIZERS_MODULE) intermediateDataSerializersModule: SerializersModule,
	koin: Koin,
): JobRunrConfiguration.JobRunrConfigurationResult {
	val logger = KotlinLogging.logger {}

	logger.info { "Initializing JobRunr without starting it..." }

	val backgroundJobServerConfiguration = BackgroundJobServerConfiguration
		.usingStandardBackgroundJobServerConfiguration()

	val result = JobRunr.configure()
		.useJsonMapper(KotlinxSerializationJsonMapper(intermediateDataSerializersModule))
		.useStorageProvider(storageProvider)
		.useJobActivator(object : JobActivator {
			override fun <T : Any> activateJob(type: Class<T>): T? = koin.getOrNull(type.kotlin)
		})
		.useBackgroundJobServer(backgroundJobServerConfiguration, false)
		.useMetrics(JobRunrMicroMeterIntegration(meterRegistry))
		.useDashboard(
			JobRunrDashboardWebServerConfiguration.usingStandardDashboardConfiguration()
				.andPort(8082)
		)
		.initialize()

	logger.info { "JobRunr initialized!" }

	return result
}

@Single
fun provideJobRunrJobRequestScheduler(configurationResult: JobRunrConfiguration.JobRunrConfigurationResult): JobRequestScheduler =
	configurationResult.jobRequestScheduler

@Single
fun provideJobRunrBackgroundJobServer(configurationResult: JobRunrConfiguration.JobRunrConfigurationResult): BackgroundJobServer =
	configurationResult.backgroundJobServer
