package me.snoty.backend.server.resources

import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import me.snoty.backend.notifications.Notification
import me.snoty.backend.notifications.NotificationService
import me.snoty.backend.server.routing.Resource
import me.snoty.backend.utils.getUser
import me.snoty.core.notification.NotificationId
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@Named("notification")
fun notificationResource(notificationService: NotificationService) = Resource {
	authenticate("jwt-auth") {
		route("notification") {
			get("list") {
				val user = call.getUser()

				call.respond(notificationService.findByUser(user.id))
			}.describe {
				responses {
					HttpStatusCode.OK {
						schema = jsonSchema<List<Notification>>()
					}
				}
			}

			get("count") {
				val user = call.getUser()

				call.respond(notificationService.unresolvedByUser(user.id))
			}

			put("{id}/resolve") {
				val user = call.getUser()
				val notificationId = call.parameters["id"]?.let(::NotificationId)
					?: return@put call.respond(HttpStatusCode.BadRequest, "Notification ID is required")

				notificationService.resolve(user.id, notificationId)

				call.respond(HttpStatusCode.NoContent)
			}

			delete("{id}") {
				val user = call.getUser()
				val notificationId = call.parameters["id"]?.let(::NotificationId)
					?: return@delete call.respond(HttpStatusCode.BadRequest, "Notification ID is required")

				val result = notificationService.delete(user.id, notificationId)

				if (result) {
					call.respond(HttpStatusCode.NoContent)
				} else {
					call.respond(HttpStatusCode.NotFound)
				}
			}
		}.describe {
			tag("notification")
		}
	}
}
