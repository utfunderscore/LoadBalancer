package org.readutf.loadbalancer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import com.velocitypowered.api.proxy.server.ServerInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.loadbalancer.alerts.NotificationManager
import org.readutf.loadbalancer.alerts.impl.ServerRegisterListener
import org.readutf.loadbalancer.alerts.impl.ServerUnregisterListener
import org.readutf.loadbalancer.balancer.OrchestratorLoadBalancer
import org.readutf.loadbalancer.listener.PlayerJoinBalancer
import org.readutf.orchestrator.shared.server.ServerAddress
import org.readutf.orchestrator.wrapper.OrchestratorApi
import java.net.InetSocketAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import java.util.UUID

@Plugin(id = "loadbalancer", name = "LoadBalancer", authors = ["utf_"], version = "1.0.0")
class LoadBalancerPlugin
    @Inject
    constructor(
        private val proxyServer: ProxyServer,
    ) {
        private val orchestratorHost = System.getenv("ORCHESTRATOR_HOST")
        private val orchestratorApi = OrchestratorApi(orchestratorHost, 9393)
        private val serverCache = mutableMapOf<ServerAddress, RegisteredServer>()

        private val logger = KotlinLogging.logger { }

        init {

            logger.info { "    ____                       " }
            logger.info { "  / __ \\_________  _  ____  __" }
            logger.info { " / /_/ / ___/ __ \\| |/_/ / / /" }
            logger.info { "/ ____/ /  / /_/ />  </ /_/ / " }
            logger.info { "/_/   /_/   \\____/_/|_|\\__, /  " }
            logger.info { "                      /____/   " }
            logger.info { "Starting loadbalancer client" }
            val properties = Properties()
            properties.load(OrchestratorLoadBalancer::class.java.getResourceAsStream("/version.properties"))

            val version = properties.getOrDefault("version", "UNKNOWN")
            val builtAt = properties.getOrDefault("buildTime", "UNKNOWN") as String

            val formattedBuildTime = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Date(builtAt.toLong()))

            println("   Running Shepard Server v$version built on $formattedBuildTime")
            NotificationManager(ObjectMapper().registerKotlinModule(), orchestratorApi)
                .listen(ServerRegisterListener(proxyServer))
                .listen(ServerUnregisterListener(proxyServer))
        }

        @Subscribe
        @Suppress("UNUSED_PARAMETER")
        fun onInit(proxyInitializeEvent: ProxyInitializeEvent) {
            proxyServer.eventManager.register(
                this,
                PlayerJoinBalancer(OrchestratorLoadBalancer(this, orchestratorApi)),
            )
        }

        @Subscribe
        fun onShutdown(event: ProxyShutdownEvent) {
//            notificationManager.shutdown()
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
