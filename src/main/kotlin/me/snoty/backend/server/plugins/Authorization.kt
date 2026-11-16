package me.snoty.backend.server.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import me.snoty.backend.authentication.Role
import me.snoty.backend.utils.requireAnyRole

val RbacPlugin = createRouteScopedPlugin(
	name = "RbacPlugin",
	createConfiguration = ::RbacPluginConfiguration,
) {
	val roles = pluginConfig.roles.toTypedArray()

	on(AuthenticationChecked) { call ->
		call.requireAnyRole(*roles)
	}
}

class RbacPluginConfiguration {
	var roles: Set<Role> = emptySet()
}
