@file:Suppress("ktlint:standard:no-wildcard-imports")

package org.readutf.loadbalancer.balancer

import com.velocitypowered.api.proxy.server.RegisteredServer
import org.readutf.loadbalancer.LoadBalancerPlugin
import org.readutf.orchestrator.shared.utils.Result
import org.readutf.orchestrator.wrapper.OrchestratorApi
import java.util.*
import kotlin.collections.LinkedHashSet

class OrchestratorLoadBalancer(
    private val plugin: LoadBalancerPlugin,
    private val orchestratorApi: OrchestratorApi,
) {
    private val connectingPlayers = mutableMapOf<UUID, LinkedHashSet<Pair<UUID, Long>>>()

    suspend fun findBestServer(
        lobbyType: String,
        playerId: UUID,
    ): Result<RegisteredServer> {
        val byTypeResponse = orchestratorApi.getServerByType(lobbyType)
        if (byTypeResponse.isError()) {
            return Result.error("Failed to connect to orchestrator api.")
        }

        val servers = byTypeResponse.get().mapNotNull { LobbyServer.parse(it) }

        if (servers.isEmpty()) {
            return Result.error("No lobby servers are available right now.")
        }

        val foundServer =
            servers.minBy {
                Math.floorDiv(getOnlinePlayers(it), 5)
            }
        println("Found server with ip: ${foundServer.server.address}")

        addPendingPlayer(foundServer.server.serverId, UUID.randomUUID())

        return Result.ok(plugin.getServer(foundServer.server.address))
    }

    private fun getOnlinePlayers(lobbyServer: LobbyServer): Int {
        synchronized(connectingPlayers) {
            val serverId = lobbyServer.server.serverId

            val serverPlayers = connectingPlayers.getOrPut(serverId) { LinkedHashSet() }

            serverPlayers.removeIf { it.second < (System.currentTimeMillis() - (1000 * 5)) || lobbyServer.online.contains(it.first) }

            connectingPlayers[serverId] = serverPlayers

            println("Pending: ${serverPlayers.size}")

            return serverPlayers.size + lobbyServer.online.size
        }
    }

    private fun addPendingPlayer(
        serverId: UUID,
        playerId: UUID,
    ) {
        synchronized(connectingPlayers) {
            val serverPlayers = connectingPlayers.getOrPut(serverId) { LinkedHashSet() }
            serverPlayers.add(playerId to System.currentTimeMillis())

            connectingPlayers[serverId] = serverPlayers
        }
    }
}
