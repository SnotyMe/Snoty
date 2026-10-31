plugins {
	id("snoty.extension-conventions")
}

dependencies {
	implementation(projects.nodeApi)

	// liquid for java
	implementation(libs.libraries.liqp)
	implementation("io.github.java-diff-utils:java-diff-utils:4.15")

	implementation(libs.libraries.ical4j)

	testImplementation(testFixtures(projects.api))
}
