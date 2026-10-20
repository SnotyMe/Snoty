package me.snoty.backend.scheduling.jobrunr

import io.github.oshai.kotlinlogging.KotlinLogging
import me.snoty.backend.utils.MANAGEMENT_EXECUTOR
import org.jobrunr.server.BackgroundJobServer
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

@Single
class JobRunrReconnecter(
	@Named(MANAGEMENT_EXECUTOR)
	private val managementPool: ScheduledExecutorService,
	private val backgroundJobServer: BackgroundJobServer
) {
	private val logger = KotlinLogging.logger {}

	private val lock = ReentrantLock()
	private var reconnectThread: Thread? = null

	fun startReconnectLoop() {
		managementPool.scheduleAtFixedRate(
			::checkAndReconnect,
			0,
			30,
			TimeUnit.SECONDS
		)
	}

	fun checkAndReconnect() {
		if (backgroundJobServer.isRunning) return

		lock.withLock {
			reconnectThread = when {
				reconnectThread?.isAlive != true -> {
					logger.warn { "JobRunr is stopped, attempting restart..." }

					thread(name = "JobRunr-Restart", start = true) {
						// will try acquiring the JobRunr start lock
						backgroundJobServer.start()
					}
				}

				else -> null // reset if the thread is not alive
			}
		}
	}
}
