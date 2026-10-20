package me.snoty.backend.database.mongo.observability.filter

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.event.CommandEvent
import com.mongodb.event.CommandStartedEvent
import me.snoty.backend.database.mongo.FLOW_EXECUTION_COLLECTION_NAME
import me.snoty.backend.database.mongo.observability.getCollectionName
import org.koin.core.annotation.Single
import java.util.concurrent.TimeUnit

@Single
class MongoObservabilityExecutionFilter : MongoObservabilityFilter {
	override fun decide(event: CommandEvent) = when {
		shouldFilter(event) -> false
		else -> null
	}

	private val executionRequestIds: Cache<Int, Unit> = Caffeine.newBuilder()
		.expireAfterWrite(30, TimeUnit.SECONDS)
		.build()

	override fun decideForLogging(event: CommandEvent): Boolean? = when {
		executionRequestIds.getIfPresent(event.requestId) != null -> return false

		shouldFilter(event) -> {
			executionRequestIds.put(event.requestId, Unit)

			return false
		}

		else -> null
	}

	private fun shouldFilter(event: CommandEvent) =
		event.commandName == "update"
				&& event is CommandStartedEvent
				&& getCollectionName(event.command, event.commandName) == FLOW_EXECUTION_COLLECTION_NAME
}
