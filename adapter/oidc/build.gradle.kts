plugins {
	id("snoty.extension-conventions")
	id("snoty.testintegration-conventions")
}

dependencies { with (libs) {
	api(ktorLibs.client.core)

	compileOnly(projects.adapter.adapterApi)
}}
