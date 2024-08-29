package org.readutf.orchestrator.spigot

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.readutf.orchestrator.client.ShepardClient
import org.readutf.orchestrator.shared.server.ServerAddress
import org.readutf.orchestrator.wrapper.OrchestratorApi
import org.readutf.orchestrator.wrapper.types.ContainerPort
import org.readutf.orchestrator.wrapper.types.NetworkAddress
import java.net.InetAddress
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LoadBalancerClient : JavaPlugin() {
    private val logger = KotlinLogging.logger { }
    private lateinit var shepardClient: ShepardClient

    private val orchestratorHost: String = System.getenv("ORCHESTRATOR_HOST")
    private val lobbyType: String = System.getenv("LOBBY_TYPE")

    private val orchestratorApi =
        OrchestratorApi(
            orchestratorHost,
            9393,
        )

    private var successfulStartup = true

    override fun onEnable() {
        logger.info { "Starting loadbalancer client v${description.version}" }
        val containerId = InetAddress.getLocalHost().getHostName()
        logger.info { "  * Orchestrator hostname: $orchestratorHost" }
        logger.info { "  * Container id: $containerId" }
//        runBlocking {
        val portResponse = runBlocking { orchestratorApi.getPort(containerId) }
        if (!portResponse.isSuccess()) {
            logger.error { "Failed to get container port from orchestrator" }
            return
        }
        val ipResponse = runBlocking { orchestratorApi.getIp(containerId) }
        if (!ipResponse.isSuccess()) {
            logger.error { "Failed to get container ip from orchestrator" }
            return
        }

        val port = findPort(portResponse.get())
        val address = findIp(ipResponse.get())

        logger.info { "  * Container port: $port" }
        logger.info { "  * Container address: $port" }

        logger.info { "Connecting to orchestrator..." }

        shepardClient = ShepardClient(orchestratorHost, 2980, ServerAddress(address, port))

        shepardClient.registerGameTypes("lobby")

        shepardClient.start().join()

        shepardClient.setAttribute("lobbyType", lobbyType)

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            shepardClient.setAttribute("online", Bukkit.getOnlinePlayers().size)
        }, 0, 5, TimeUnit.SECONDS)
//        }
    }

    private fun findIp(get: List<NetworkAddress>): String = get[0].ip

    fun findPort(containerPorts: List<ContainerPort>) = containerPorts[0].privatePort
}
