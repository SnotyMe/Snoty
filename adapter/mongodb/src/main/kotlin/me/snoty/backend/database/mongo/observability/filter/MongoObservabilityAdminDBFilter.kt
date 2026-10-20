package me.snoty.backend.database.mongo.observability.filter

import com.mongodb.event.CommandEvent
import org.koin.core.annotation.Single

@Single
class MongoObservabilityAdminDBFilter : MongoObservabilityFilter {
	override fun decide(event: CommandEvent) = when (event.databaseName) {
		"admin" -> false
		else -> null
	}
}
