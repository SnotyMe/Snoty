import org.tomlj.Toml

pluginManagement {
	repositories {
		gradlePluginPortal()
	}
}

buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.tomlj:tomlj:2.2.0")
	}
}

val libsToml = generateSequence<File>(File(".")) { it.parentFile }
	.map { it.resolve("gradle/libs.versions.toml") }
	.first { it.isFile }
val toml = Toml.parse(libsToml.toPath())

dependencyResolutionManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()

		// https://github.com/node-gradle/gradle-node-plugin/blob/main/docs/faq.md#is-this-plugin-compatible-with-centralized-repositories-declaration
		ivy {
			name = "Node.js"
			setUrl("https://nodejs.org/dist/")
			patternLayout {
				artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
			}
			metadataSources {
				artifact()
			}
			content {
				includeModule("org.nodejs", "node")
			}
		}
	}

	versionCatalogs {
		create("ktorLibs") {
			val ktorVersion = requireNotNull(toml.getString("versions.ktor")) {
				"Missing versions.ktor in libs.versions.toml"
			}

			from("io.ktor:ktor-version-catalog:$ktorVersion")
		}
	}
}
