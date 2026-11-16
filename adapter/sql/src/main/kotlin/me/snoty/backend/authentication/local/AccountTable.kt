package me.snoty.backend.authentication.local

import me.snoty.backend.database.sql.utils.userId
import me.snoty.core.user.UserId
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.koin.core.annotation.Single
import kotlin.uuid.Uuid

@Single
class AccountTable : IdTable<UserId>("localauth_account") {
	override val id = userId("id").clientDefault {
		// v4 is used to avoid leaking the account creation time
		UserId(Uuid.generateV4().toString())
	}.entityId()

	val email = text("email").uniqueIndex()
	val passwordHash = text("password_hash")
	val displayName = text("display_name")
	val roles = array<String>("roles")
}
