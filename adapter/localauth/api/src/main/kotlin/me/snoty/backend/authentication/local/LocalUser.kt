package me.snoty.backend.authentication.local

import me.snoty.backend.authentication.User
import me.snoty.core.user.UserId

data class LocalUser(
	val id: UserId,
	val email: String,
	val displayName: String,
) {
	fun toUser() = User(
		id = id,
		name = displayName,
		email = email,
	)
}
