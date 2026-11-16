package me.snoty.backend.server.resources

import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import kotlinx.serialization.json.Json
import me.snoty.backend.authentication.Role
import me.snoty.backend.build.BuildInfo
import me.snoty.backend.config.Config
import me.snoty.backend.config.Environment
import me.snoty.backend.injection.getFromAllScopes
import me.snoty.backend.server.plugins.RbacPlugin
import me.snoty.backend.server.routing.API_BASE_PATH
import me.snoty.backend.server.routing.Resource
import org.koin.core.Koin
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.koin.ktor.ext.get as getDependency

@Single
@Named("openApiResource")
fun openApiResource(environment: Environment, config: Config) = Resource {
	val apiRoot = this

	val openApiEnabled = config.server.openApi.enabled ?: environment.isDev()
	if (!openApiEnabled) return@Resource

	val koin: Koin = getDependency()

	val openApiAuthorized = config.server.openApi.authorized ?: environment.isProd()
	if (openApiAuthorized) {
		authenticate("jwt-auth") {
			install(RbacPlugin) {
				roles = setOf(Role.ADMIN)
			}

			openApiRoute(koin, apiRoot)
		}
	} else {
		openApiRoute(koin, apiRoot)
	}
}

private fun Route.openApiRoute(koin: Koin, apiRoot: Route) {
	val buildInfo: BuildInfo = getDependency()
	val extraSchemas: List<JsonSchema> = koin.getFromAllScopes()

	val info = OpenApiInfo(title = buildInfo.application, version = buildInfo.version)
	val components = Components(
		schemas = extraSchemas.associateBy { it.title ?: error("All extra schemas must have a title") }
	)

	val openApiJson = Json {
		prettyPrint = true
		encodeDefaults = false
	}

	get("/openapi.json") {
		var doc = OpenApiDoc(
			info = info,
			servers = listOf(Server(url = API_BASE_PATH)),
			components = components,
		) + apiRoot.descendants()
		val paths = doc.paths.mapKeys { (key, _) ->
			key.substringAfter(API_BASE_PATH)
		}.toSortedMap()
		doc = doc.copy(paths = paths)
		call.respondText(openApiJson.encodeToString(doc), contentType = ContentType.Application.Json)
	}.describe {
		responses {
			HttpStatusCode.OK {
				schema = jsonSchema<GenericElement>()
			}
		}
	}

	swaggerUI(path = "/swagger") {
		this.info = info
		this.components = components
		this.source = OpenApiDocSource.Routing(ContentType.Application.Json)
	}
}
