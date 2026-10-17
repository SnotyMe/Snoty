package me.snoty.backend.server.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.request.*
import me.snoty.backend.utils.ForbiddenException
import me.snoty.backend.utils.http.XSnoty

val SelfCallRejectionPlugin = createApplicationPlugin(name = "SelfCallRejection") {
    on(CallSetup) { call ->
        call.request.header(HttpHeaders.XSnoty)
            ?: return@on

        throw ForbiddenException("Calls from Snoty are not permitted")
    }
}
