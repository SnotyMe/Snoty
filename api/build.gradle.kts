plugins {
    alias(libs.plugins.kotlin.serialization)
    id("snoty.publish-conventions")
    id("snoty.publish-repo-conventions")
    id("snoty.binary-compatibility-conventions")
    `java-test-fixtures`
}

dependencies { with(libs) {
    api(configuration.hoplite.core)

    api(libraries.kotlinx.serialization)
    api(libraries.kotlinx.datetime)

    api(libraries.jobrunr) {
        exclude(group = "io.insert-koin", module = "koin-core")
    }
    api(libraries.jobrunr.kotlin) {
        exclude(group = "io.insert-koin", module = "koin-core")
    }

    api(log.kotlinLogging)
    api(log.logback)

    api(ktorLibs.openapiSchema)

    api(ktorLibs.client.core)
    api(ktorLibs.client.okhttp)
    api(ktorLibs.client.contentNegotiation)

    api(ktorLibs.server.core)
    api(ktorLibs.server.auth)
    api(ktorLibs.server.auth.jwt)
    api(ktorLibs.server.routingOpenapi)

    api(ktorLibs.serialization.kotlinx.json)
    api(monitoring.ktor.opentelemetry)

    api(libraries.openfeature)
    api(libraries.caffeine)
    api(libraries.jakarta.mail)

    // DO NOT use in anything but bson-related code
    implementation(database.mongodb)
    api(libraries.bson.kotlin)
    api(libraries.bson.kotlinx)

    api(monitoring.micrometer)
    api(monitoring.opentelemetry.api)
    api(monitoring.opentelemetry.context)
    api(monitoring.opentelemetry.semconv)
    implementation(monitoring.opentelemetry.semconv.incubating)

    testFixturesImplementation(tests.json)
    testFixturesApi(tests.junit)
    testFixturesRuntimeOnly(tests.junit.launcher)
    testFixturesApi(kotlin("test"))
    testFixturesApi(koin.test)
    testFixturesApi(tests.mockk)
    testFixturesApi(tests.testcontainers.junit)
}}

tasks.test {
    useJUnitPlatform()
}
