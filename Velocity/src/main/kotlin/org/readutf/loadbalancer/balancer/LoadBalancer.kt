package org.readutf.loadbalancer.balancer

import com.velocitypowered.api.proxy.server.RegisteredServer
import org.readutf.orchestrator.shared.utils.Result

interface LoadBalancer {
    suspend fun findBestServer(lobbyType: String): Result<RegisteredServer>
}
