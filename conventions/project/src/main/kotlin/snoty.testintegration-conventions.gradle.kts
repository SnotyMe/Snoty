@file:Suppress("UnstableApiUsage")

plugins {
	kotlin("jvm")
	`jvm-test-suite`
}

lateinit var testIntegration: NamedDomainObjectProvider<JvmTestSuite>

testing {
	suites {
		val test = named<JvmTestSuite>("test") {
			useJUnitJupiter()
		}

		testIntegration = register<JvmTestSuite>("testIntegration") {
			dependencies {
				implementation(project())
				implementation(sourceSets.test.get().output)
			}
			sources.compileClasspath += test.get().sources.compileClasspath
			sources.runtimeClasspath += test.get().sources.runtimeClasspath

			targets {
				all {
					testTask.configure {
						shouldRunAfter(test)
					}
				}
			}
		}

		withType<JvmTestSuite> {
			useJUnitJupiter()
			dependencies {
				implementation(testFixtures(project(":api")))
			}

			tasks.withType<Test>().configureEach {
				testLogging {
					events("failed", "standardOut", "standardError")
					exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
					showStandardStreams = true
				}
			}
		}
	}
}

tasks.check {
	dependsOn(testIntegration)
}
