package me.snoty.backend.server.routing

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.routing.*

const val API_BASE_PATH = "/api"

fun Application.addResources(resources: List<Resource>) = routing {
	val logger = KotlinLogging.logger {}

	route(API_BASE_PATH) {
		resources.forEach {
			logger.debug { "Adding resource $it" }
			with (it) { register() }
		}
	}
}
