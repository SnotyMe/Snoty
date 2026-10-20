package me.snoty.backend.scheduling.jobrunr

import io.github.oshai.kotlinlogging.KotlinLogging
import me.snoty.backend.scheduling.JobTriggerResult
import me.snoty.backend.scheduling.Scheduler
import me.snoty.backend.scheduling.SnotyJob
import org.jobrunr.jobs.Job
import org.jobrunr.jobs.states.StateName
import org.jobrunr.scheduling.JobBuilder
import org.jobrunr.scheduling.JobBuilder.aJob
import org.jobrunr.scheduling.JobRequestScheduler
import org.jobrunr.scheduling.RecurringJobBuilder.aRecurringJob
import org.jobrunr.server.BackgroundJobServer
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible

@Single
@Named("adapter")
class JobRunrScheduler(
	private val jobRequestScheduler: JobRequestScheduler,
	private val backgroundJobServer: BackgroundJobServer,
	private val storageProvider: SnotyJobRunrStorageProvider,
	private val jobRunrReconnecter: JobRunrReconnecter,
) : Scheduler {
	private val logger = KotlinLogging.logger {}

	private lateinit var build: KFunction<Job>
	private lateinit var saveJob: KFunction<*>

	@Suppress("UNCHECKED_CAST")
	override fun start() {
		logger.info { "Starting JobRunr Scheduler..." }

		backgroundJobServer.start()
		jobRunrReconnecter.startReconnectLoop()

		build = JobBuilder::class
			.declaredMemberFunctions
			.single { it.name == "build" && it.valueParameters.isEmpty() }
			.apply {
				isAccessible = true
			} as KFunction<Job>

		saveJob = jobRequestScheduler::class
			.memberFunctions
			.single { it.name == "saveJob" }
			.apply {
				isAccessible = true
			}

		logger.info { "JobRunr Scheduler started!" }
	}

	override fun scheduleRecurringJob(job: SnotyJob) {
		jobRequestScheduler.createRecurrently(
			aRecurringJob()
				.withId(job.recurringJobId)
				.withName(job.name)
				.withAmountOfRetries(job.retries)
				.withJobRequest(job.request)
				.withSchedule(job.schedule)
		)
	}

	override fun triggerJob(job: SnotyJob): JobTriggerResult? {
		val recurringJobId = job.recurringJobId

		val jobBuilder = aJob()
			.withName(job.name)
			.withAmountOfRetries(job.retries)
			.withJobRequest(job.request)

		if (recurringJobId != null && pendingJobForRecurringJobExists(recurringJobId)) {
			logger.info { "Pending Job for ${job.recurringJobId} already exists, not scheduling" }
			return null
		}

		val job = build
			.call(jobBuilder)
			.apply {
				setRecurringJobId(recurringJobId)
			}

		backgroundJobServer.processJob(job)

		return JobTriggerResult(
			jobId = job.id.toString(),
		)
	}

	override fun deleteRecurringJob(id: String) {
		jobRequestScheduler.deleteRecurringJob(id)
	}

	override fun pendingJobForRecurringJobExists(id: String) =
		storageProvider.recurringJobExists(id, StateName.ENQUEUED, StateName.PROCESSING)
}
