import java.util.*

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("xyz.jpenilla.run-velocity") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.readutf.orchestrator"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven {
        name = "utfunderscore"
        url = uri("https://repo.readutf.org/releases")
        credentials(PasswordCredentials::class)
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))

    // Add velocity 3.3.0
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation("com.github.Revxrsal.Lamp:common:3.2.1")
    implementation("com.github.Revxrsal.Lamp:velocity:3.2.1")

    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")

    implementation("org.readutf.orchestrator:api-wrapper:1.7.9")
    implementation("org.readutf.orchestrator:shared:1.7.9")

    // Caffeine cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // add kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC.2")

    implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")
}

tasks {

    compileJava {
        options.compilerArgs.add("-parameters")
    }

    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("OrchestratorHook.jar")
    }

    runVelocity {
        // Configure the Velocity version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        velocityVersion("3.3.0-SNAPSHOT")

        jvmArgs("-DORCHESTRATOR_HOST=89.33.85.41")
    }

    register("createProperties") {
        doLast {
            val propertiesFile = file("$buildDir/resources/main/version.properties")
            propertiesFile.parentFile.mkdirs()
            propertiesFile.writer().use { writer ->
                val properties = Properties()
                properties["version"] = project.version.toString()
                properties["buildTime"] = System.currentTimeMillis().toString()
                properties.store(writer, null)
            }
        }
    }
    named("classes") {
        dependsOn("createProperties")
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
