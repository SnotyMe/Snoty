package me.snoty.backend.authentication.local

import me.snoty.backend.authentication.Role
import me.snoty.core.user.UserId

interface LocalAuthPersistenceService {
	suspend fun createUser(email: String, passwordHash: String, displayName: String): LocalUser

	suspend fun getUserById(userId: UserId): LocalUser?
	suspend fun getUserByEmail(email: String): LocalUser?

	/**
	 * In one operation to prevent timing attacks by short-circuiting the password lookup
	 */
	suspend fun getUserWithPasswordHash(email: String): Pair<LocalUser, String>?

	suspend fun getRolesByUserId(userId: UserId): List<Role>
}
