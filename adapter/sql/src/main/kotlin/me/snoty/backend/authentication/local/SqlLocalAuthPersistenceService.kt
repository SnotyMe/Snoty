package me.snoty.backend.authentication.local

import me.snoty.backend.authentication.Role
import me.snoty.backend.database.sql.suspendTransaction
import me.snoty.core.user.UserId
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.koin.core.annotation.Single

@Single
class SqlLocalAuthPersistenceService(
	private val db: Database,
	private val accountTable: AccountTable,
) : LocalAuthPersistenceService {
	override suspend fun createUser(
		email: String,
		passwordHash: String,
		displayName: String
	): LocalUser = db.suspendTransaction {
		accountTable.insertReturning {
			it[accountTable.email] = email
			it[accountTable.passwordHash] = passwordHash
			it[accountTable.displayName] = displayName
		}.first().toLocalUser()
	}

	override suspend fun getUserById(userId: UserId): LocalUser? = db.suspendTransaction {
		accountTable.selectAll().where { accountTable.id eq userId }
			.firstOrNull()
			?.toLocalUser()
	}

	override suspend fun getUserByEmail(email: String): LocalUser? = db.suspendTransaction {
		accountTable.selectAll().where { accountTable.email eq email }
			.firstOrNull()
			?.toLocalUser()
	}

	override suspend fun getUserWithPasswordHash(email: String): Pair<LocalUser, String>? = db.suspendTransaction {
		accountTable.selectAll().where { accountTable.email eq email }
			.firstOrNull()
			?.let { Pair(it.toLocalUser(), it[accountTable.passwordHash]) }
	}

	override suspend fun getRolesByUserId(userId: UserId): List<Role> = db.suspendTransaction {
		accountTable.selectAll().where { accountTable.id eq userId }
			.firstOrNull()
			?.let { it[accountTable.roles].map(::Role) }
			?: emptyList()
	}

	private fun ResultRow.toLocalUser() = LocalUser(
		id = this[accountTable.id].value,
		email = this[accountTable.email],
		displayName = this[accountTable.displayName],
	)
}
