package me.snoty.backend.notification

import com.mongodb.client.model.IndexModel
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.model.Filters
import com.mongodb.kotlin.client.model.Indexes
import com.mongodb.kotlin.client.model.Sorts
import com.mongodb.kotlin.client.model.Updates
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.snoty.backend.database.mongo.objectId
import me.snoty.backend.notifications.Notification
import me.snoty.backend.notifications.NotificationAttributes
import me.snoty.backend.notifications.NotificationService
import me.snoty.core.notification.NotificationId
import me.snoty.core.user.UserId
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.koin.core.annotation.Single
import kotlin.time.Clock

@Single
class MongoNotificationService(
	db: MongoDatabase,
) : NotificationService {
	private val collection = db.getCollection<MongoNotification>("notifications")

	init {
		@OptIn(DelicateCoroutinesApi::class)
		GlobalScope.launch(Dispatchers.IO) {
			collection.createIndexes(
				listOf(
					IndexModel(
						Indexes.descending(MongoNotification::userId, MongoNotification::attributes, MongoNotification::resolvedAt),
						IndexOptions().unique(true)
					),
				)
			).collect()
		}
	}

	private fun buildAttributesFilter(attributes: NotificationAttributes): Bson {
		val document = Document(attributes)
		return Filters.eq(MongoNotification::attributes, document)
	}

	private fun buildUnresolvedFilter(userId: UserId, matcher: Bson) = Filters.and(
		Filters.eq(MongoNotification::userId, userId),
		matcher,
		Filters.eq(MongoNotification::resolvedAt, null),
	)

	override suspend fun send(userId: UserId, attributes: NotificationAttributes, title: String, description: String?) {
		val filter = buildUnresolvedFilter(userId, buildAttributesFilter(attributes))
		val update = Updates.combine(
			Updates.setOnInsert(MongoNotification::_id, ObjectId()),
			Updates.setOnInsert(MongoNotification::userId, userId),
			Updates.setOnInsert(MongoNotification::attributes, Document(attributes)),
			Updates.set(MongoNotification::lastSeenAt, Clock.System.now()),
			Updates.set(MongoNotification::title, title),
			Updates.set(MongoNotification::description, description),
			Updates.inc(MongoNotification::count, 1)
		)
		collection.updateOne(filter, update, UpdateOptions().upsert(true))
	}

	override suspend fun resolve(userId: UserId, id: NotificationId) {
		val filter = buildUnresolvedFilter(userId, Filters.eq(MongoNotification::_id, id.value.objectId))
		val update = Updates.set(MongoNotification::resolvedAt, Clock.System.now())
		collection.updateOne(filter, update)
	}

	override suspend fun resolve(userId: UserId, attributes: NotificationAttributes) {
		val filter = buildUnresolvedFilter(userId, buildAttributesFilter(attributes))
		val update = Updates.set(MongoNotification::resolvedAt, Clock.System.now())
		collection.updateOne(filter, update)
	}

	override fun findByUser(userId: UserId): Flow<Notification> =
		collection.find(Filters.eq(MongoNotification::userId, userId))
			.sort(Sorts.descending(MongoNotification::lastSeenAt))
			.map(MongoNotification::toNotification)

	override suspend fun unresolvedByUser(userId: UserId): Long =
		collection.countDocuments(
			Filters.and(
				Filters.eq(MongoNotification::userId, userId),
				Filters.eq(MongoNotification::resolvedAt, null)
			)
		)

	override suspend fun delete(userId: UserId, id: NotificationId): Boolean {
		val result = collection.deleteOne(
			Filters.and(
				Filters.eq(MongoNotification::userId, userId),
				Filters.eq(MongoNotification::_id, id.value.objectId)
			)
		)
		return result.deletedCount > 0
	}
}
