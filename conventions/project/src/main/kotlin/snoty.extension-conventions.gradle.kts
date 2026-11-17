plugins {
	kotlin("jvm")
	id("com.google.devtools.ksp")
}

apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
apply(plugin = "org.jetbrains.kotlinx.kover")

fun VersionCatalog.getLibrary(name: String) =
	findLibrary(name).get().get()
fun VersionCatalog.getModule(name: String) =
	getLibrary(name).module

dependencies {
	implementation(project(":api"))

	ksp(project(":plugin"))

	val ktorLibs = project.rootProject.extensions
		.getByType<VersionCatalogsExtension>()
		.named("ktorLibs")

	kotlinCompilerPluginClasspath(ktorLibs.getLibrary("compilerPlugin"))
}

sourceSets.test.configure {
	val api = project(":api").sourceSets.test.get().output
	compileClasspath += api
	runtimeClasspath += api
}

tasks.test {
	useJUnitPlatform()
}

tasks.compileKotlin.configure {
	compilerOptions {
		freeCompilerArgs.addAll(
			"-P", "plugin:io.ktor.ktor-compiler-plugin:openApiEnabled=true",
			"-P", "plugin:io.ktor.ktor-compiler-plugin:openApiCodeInference=true",
			"-P", "plugin:io.ktor.ktor-compiler-plugin:openApiOnlyCommented=false"
		)
	}
}
