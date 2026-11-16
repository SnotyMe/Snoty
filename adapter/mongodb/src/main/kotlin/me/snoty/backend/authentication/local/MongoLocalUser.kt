package me.snoty.backend.authentication.local

import me.snoty.core.user.UserId

data class MongoLocalUser(
	val _id: UserId,
	val email: String,
	val passwordHash: String,
	val displayName: String,
	val roles: List<String>,
) {
	fun toLocalUser() = LocalUser(
		id = _id,
		email = email,
		displayName = displayName,
	)
}
