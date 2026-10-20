package me.snoty.backend.database.mongo.observability.logging

import com.mongodb.event.*
import io.github.oshai.kotlinlogging.KotlinLogging
import me.snoty.backend.config.Environment
import me.snoty.backend.database.mongo.observability.filter.MongoObservabilityFilter
import me.snoty.backend.database.mongo.observability.getCollectionName
import me.snoty.backend.utils.ifDev
import me.snoty.backend.utils.otherwise
import org.koin.core.annotation.Single
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

@Single
class MongoCommandLogger(
	private val filters: List<MongoObservabilityFilter>,
	private val environment: Environment,
) : CommandListener {
	private val logger = KotlinLogging.logger {}

	override fun commandStarted(event: CommandStartedEvent) {
		logTraceEvent(event, "started") {
			val collectionNamePart = getCollectionName(event.command, event.commandName)
				?.let { "collectionName=$it" }
			val commandPart = environment.ifDev<String?> { "command=${event.command.toJson()}" } otherwise { null }
			listOfNotNull(collectionNamePart, commandPart).joinToString(" ")
		}
	}

	override fun commandSucceeded(event: CommandSucceededEvent) {
		logTraceEvent(event, "succeeded") {
			val responsePart = environment ifDev { "response=${event.response.toJson()}" } otherwise { "" }
			"duration=${event.getElapsedTime(TimeUnit.MILLISECONDS).milliseconds} $responsePart"
		}
	}

	override fun commandFailed(event: CommandFailedEvent) {
		logTraceEvent(event, "failed") {
			"error=${event.throwable.message}"
		}
	}

	private fun logTraceEvent(event: CommandEvent, type: String, suffix: () -> String) {
		if (filters.any { it.decideForLogging(event) == false }) {
			return
		}

		logger.trace {
			"""
				Command "${event.commandName}" $type. requestId=${event.requestId} connectionId=${event.connectionDescription.connectionId} ${suffix()}
			""".trimIndent().trim()
		}
	}
}
