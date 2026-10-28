plugins {
	id("snoty.extension-conventions")
}

dependencies {
	compileOnly(projects.api)

	runtimeOnly(libs.libraries.angus)
}
