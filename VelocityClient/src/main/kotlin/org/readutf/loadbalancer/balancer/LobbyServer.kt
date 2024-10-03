package org.readutf.loadbalancer.balancer

import org.readutf.orchestrator.shared.server.Server
import java.util.UUID

data class LobbyServer(
    val lobbyType: String,
    val online: Collection<UUID>,
    val server: Server,
) {
    companion object {
        fun parse(server: Server): LobbyServer? {
            val lobbyTypeAttribute = server.attributes["lobbyType"]
            val onlineAttribute = server.attributes["players"]

            if (lobbyTypeAttribute == null || lobbyTypeAttribute.data !is String) {
                println("Server does not have attribute 'lobbyType'")
                return null
            }

            if (onlineAttribute == null || onlineAttribute.data !is Collection<*>) {
                println("Server does not have attribute 'online'")
                return null
            }

            return kotlin
                .runCatching {
                    @Suppress("UNCHECKED_CAST")
                    LobbyServer(
                        lobbyTypeAttribute.data as String,
                        onlineAttribute.data as Collection<UUID>,
                        server,
                    )
                }.getOrNull()
        }
    }
}
