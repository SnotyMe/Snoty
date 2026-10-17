@file:Suppress("UnusedReceiverParameter") // namespacing

package me.snoty.backend.utils.http

import io.ktor.http.HttpHeaders

val HttpHeaders.XSnoty get() = "X-Snoty"

val HttpHeaders.XSnotyNodeId get() = "X-Snoty-Node-Id"
