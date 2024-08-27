import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

group = "org.readutf.orchestrator"
version = "1.1.0"

repositories {
    mavenLocal()
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

tasks.named<ShadowJar>("shadowJar") {
    finalizedBy("copyJar")
}

tasks.register("copyJar") {
    val target = file("/docker/Spigot-1.0-SNAPSHOT-all.jar")
    if (target.exists()) {
        target.delete()
    }
    file("/build/libs/Spigot-1.0-SNAPSHOT-all.jar").copyTo(target)
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.github.Revxrsal.Lamp:common:3.2.1")
    implementation("com.github.Revxrsal.Lamp:velocity:3.2.1")

    implementation("org.readutf.orchestrator:client:1.6.2")
    implementation("org.readutf.orchestrator:api-wrapper:1.6.2")

    implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks {
    test {
        useJUnitPlatform()
    }

    compileJava {
        options.compilerArgs.add("-parameters")
        options.compilerArgs.add("-java-parameters")
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21")
    }
}

bukkitPluginYaml {
    name = "LoadBalancer"
    main = "org.readutf.orchestrator.spigot.LoadBalancerClient"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("utf_")
    apiVersion = "1.20"
}

kotlin {
    jvmToolchain(21)
}
