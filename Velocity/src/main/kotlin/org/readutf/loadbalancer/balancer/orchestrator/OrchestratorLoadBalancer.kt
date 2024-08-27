package org.readutf.loadbalancer.balancer.orchestrator

import com.velocitypowered.api.proxy.server.RegisteredServer
import org.readutf.loadbalancer.LoadBalancerPlugin
import org.readutf.loadbalancer.balancer.LoadBalancer
import org.readutf.orchestrator.shared.server.Server
import org.readutf.orchestrator.shared.utils.Result
import org.readutf.orchestrator.wrapper.OrchestratorApi

class OrchestratorLoadBalancer(
    val plugin: LoadBalancerPlugin,
) : LoadBalancer {
    private val orchestratorApi = OrchestratorApi(System.getenv("ORCHESTRATOR_HOST"), 9393)
//    private val orchestratorApi = OrchestratorApi("89.33.85.41", 9393)

    override suspend fun findBestServer(lobbyType: String): Result<RegisteredServer> {
        val byTypeResponse = orchestratorApi.getServerByType("lobby")
        if (byTypeResponse.isError()) {
            return Result.error("Failed to connect to orchestrator api.")
        }

        val byType = byTypeResponse.get().mapNotNull(LobbyServer.Companion::parse)

        if (byType.isEmpty()) {
            return Result.error("No lobby servers are available right now.")
        }

        val foundServer = byType.minBy { it.online }
        println("Found server with ip: ${foundServer.server.address}")
        return Result.ok(plugin.getServer(foundServer.server.address))
    }

    data class LobbyServer(
        val lobbyType: String,
        val online: Int,
        val server: Server,
    ) {
        companion object {
            fun parse(server: Server): LobbyServer? {
                val lobbyTypeAttribute = server.attributes["lobbyType"]
                val onlineAttribute = server.attributes["online"]

                if (lobbyTypeAttribute == null) {
                    println("Server does not have attribute 'lobbyType'")
                    return null
                }

                if (onlineAttribute == null) {
                    println("Server does not have attribute 'online'")
                    return null
                }

                return kotlin
                    .runCatching {
                        LobbyServer(
                            server.attributes["lobbyType"]?.data as String,
                            server.attributes["online"]?.data as Int,
                            server,
                        )
                    }.getOrNull()
            }
        }
    }
}
