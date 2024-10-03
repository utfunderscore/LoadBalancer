package org.readutf.loadbalancer.alerts.impl

import com.velocitypowered.api.proxy.ProxyServer
import org.readutf.loadbalancer.alerts.NotificationListener
import org.readutf.loadbalancer.utils.toComponent
import org.readutf.orchestrator.shared.notification.impl.ServerUnregisterNotification

class ServerUnregisterListener(
    private val proxyServer: ProxyServer,
) : NotificationListener<ServerUnregisterNotification> {
    override fun onNotification(notification: ServerUnregisterNotification) {
        proxyServer.allPlayers // .filter { it.hasPermission("discordmc.alert.servercreate") }
            .forEach {
                val shortId = notification.serverId.toString().substring(0, 8)

                it.sendMessage(
                    "&7[&9Orchestrator&7] &7Server &e$shortId &7has been unregistered!".toComponent(),
                )
            }
    }
}
