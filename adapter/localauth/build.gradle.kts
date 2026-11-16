@file:Suppress("UnstableApiUsage")

plugins {
	id("snoty.extension-conventions")
}

dependencies { with(libs) {
	compileOnly(projects.api)
	compileOnly(projects.adapter.adapterApi)

	implementation(projects.adapter.localauth.localauthApi)

	implementation(libraries.spring.security.crypto)
	implementation(libraries.bouncycastle.bcprov)
}}
