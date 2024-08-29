plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.readutf.orchestrator"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }

    maven {
        name = "utfunderscore"
        url = uri("https://reposilite.readutf.org/releases")
        credentials(PasswordCredentials::class)
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.readutf.dev.MainKt"
    }
}

dependencies {
    implementation("org.readutf.orchestrator:client:1.6.4")
    implementation("org.readutf.orchestrator:api-wrapper:1.6.4")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
