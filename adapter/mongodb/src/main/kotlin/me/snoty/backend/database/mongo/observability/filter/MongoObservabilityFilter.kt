package me.snoty.backend.database.mongo.observability.filter

import com.mongodb.event.CommandEvent

interface MongoObservabilityFilter {
	/**
	 * @return `true` if the command should be traced and logged, `false` if it should be ignored, `null` if the filter does not care
	 */
	fun decide(event: CommandEvent): Boolean?

	fun decideForLogging(event: CommandEvent): Boolean? = decide(event)
}
