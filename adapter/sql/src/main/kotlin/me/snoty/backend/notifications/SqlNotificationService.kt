package me.snoty.backend.notifications

import me.snoty.backend.database.sql.flowTransaction
import me.snoty.backend.database.sql.suspendTransaction
import me.snoty.core.notification.NotificationId
import me.snoty.core.user.UserId
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.jdbc.*
import org.koin.core.annotation.Single

@Single
class SqlNotificationService(private val db: Database, private val table: NotificationTable) : NotificationService {
	override suspend fun send(userId: UserId, attributes: NotificationAttributes, title: String, description: String?): Unit = db.suspendTransaction {
		table.upsert(
			table.userId, table.attributes, table.open,
			onUpdate = {
				it[table.count] = table.count + 1
				it[table.lastSeenAt] = CurrentTimestamp

				it[table.title] = title
				it[table.description] = description
			}
		) {
			it[table.userId] = userId
			it[table.attributes] = attributes
			it[table.open] = true

			it[table.title] = title
			it[table.description] = description
		}
	}

	override suspend fun resolve(userId: UserId, attributes: NotificationAttributes) =
		resolveImpl(userId) { table.attributes eq attributes }

	override suspend fun resolve(userId: UserId, id: NotificationId) =
		resolveImpl(userId) { table.id eq id }

	private suspend fun resolveImpl(userId: UserId, matcher: () -> Op<Boolean>): Unit = db.suspendTransaction {
		table.update(where = {
			(table.open eq true) and (table.userId eq userId) and matcher()
		}) {
			it[table.open] = null // null, not false, to allow multiple resolved notifications (null != null)
			it[table.resolvedAt] = CurrentTimestamp
		}
	}

	override fun findByUser(userId: UserId) = db.flowTransaction {
		table.selectAll()
			.where { table.userId eq userId }
			.orderBy(table.lastSeenAt, SortOrder.DESC)
			.map {
				Notification(
					id = it[table.id].value,
					userId = it[table.userId],
					attributes = it[table.attributes],
					resolvedAt = it[table.resolvedAt],
					lastSeenAt = it[table.lastSeenAt],
					count = it[table.count],
					title = it[table.title],
					description = it[table.description],
				)
			}
	}

	override suspend fun unresolvedByUser(userId: UserId): Long = db.suspendTransaction {
		table.selectAll()
			.where { (table.userId eq userId) and (table.open eq true) }
			.count()
	}

	override suspend fun delete(userId: UserId, id: NotificationId) = db.suspendTransaction {
		table.deleteWhere { (table.userId eq userId) and (table.id eq id) } > 0
	}
}
