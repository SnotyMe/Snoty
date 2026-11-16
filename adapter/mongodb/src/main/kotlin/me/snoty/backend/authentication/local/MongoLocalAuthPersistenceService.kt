package me.snoty.backend.authentication.local

import com.mongodb.client.model.IndexOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.model.Filters.eq
import com.mongodb.kotlin.client.model.Indexes
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.snoty.backend.authentication.Role
import me.snoty.core.user.UserId
import org.koin.core.annotation.Single
import kotlin.uuid.Uuid

@Single
class MongoLocalAuthPersistenceService(
	db: MongoDatabase,
) : LocalAuthPersistenceService {
	private val collection = db.getCollection<MongoLocalUser>("localauth.accounts")

	init {
		@OptIn(DelicateCoroutinesApi::class)
		GlobalScope.launch(Dispatchers.IO) {
			collection.createIndex(
				Indexes.ascending(MongoLocalUser::email),
				IndexOptions().unique(true)
			)
		}
	}

	override suspend fun createUser(
		email: String,
		passwordHash: String,
		displayName: String
	): LocalUser {
		val user = MongoLocalUser(
			_id = UserId(Uuid.generateV4().toString()),
			email = email,
			passwordHash = passwordHash,
			displayName = displayName,
			roles = emptyList(),
		)

		collection.insertOne(user)

		return user.toLocalUser()
	}

	override suspend fun getUserById(userId: UserId): LocalUser? =
		collection.find(MongoLocalUser::_id eq userId)
			.firstOrNull()
			?.toLocalUser()

	override suspend fun getUserByEmail(email: String): LocalUser? =
		collection.find(MongoLocalUser::email eq email)
			.firstOrNull()
			?.toLocalUser()

	override suspend fun getUserWithPasswordHash(email: String): Pair<LocalUser, String>? =
		collection.find(MongoLocalUser::email eq email)
			.firstOrNull()
			?.let { it.toLocalUser() to it.passwordHash }

	override suspend fun getRolesByUserId(userId: UserId): List<Role> =
		collection.find(MongoLocalUser::_id eq userId)
			.firstOrNull()
			?.roles
			?.map { Role(it) }
			?: emptyList()
}
