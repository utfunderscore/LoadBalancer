package org.readutf.loadbalancer

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import com.velocitypowered.api.proxy.server.ServerInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.readutf.loadbalancer.balancer.orchestrator.OrchestratorLoadBalancer
import org.readutf.loadbalancer.listener.PlayerJoinBalancer
import org.readutf.orchestrator.shared.server.ServerAddress
import java.net.InetSocketAddress
import java.util.UUID

@Plugin(id = "loadbalancer", name = "LoadBalancer", authors = ["utf_"], version = "1.0.0")
class LoadBalancerPlugin
    @Inject
    constructor(
        private val proxyServer: ProxyServer,
    ) {
        private val logger = KotlinLogging.logger { }

        val serverCache = mutableMapOf<ServerAddress, RegisteredServer>()

        @Subscribe
        fun onInit(proxyInitializeEvent: ProxyInitializeEvent) {
            proxyServer.eventManager.register(this, PlayerJoinBalancer(OrchestratorLoadBalancer(this)))
        }

        companion object {
            val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        }

        fun getServer(serverAddress: ServerAddress) =
            serverCache.getOrPut(serverAddress) {
                proxyServer.registerServer(
                    ServerInfo(
                        UUID.randomUUID().toString().substring(0, 8),
                        InetSocketAddress(serverAddress.host, serverAddress.port),
                    ),
                )
            }
    }
