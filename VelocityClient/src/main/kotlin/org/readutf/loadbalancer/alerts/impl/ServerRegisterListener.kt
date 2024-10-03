package org.readutf.loadbalancer.alerts.impl

import com.velocitypowered.api.proxy.ProxyServer
import org.readutf.loadbalancer.alerts.NotificationListener
import org.readutf.loadbalancer.utils.toComponent
import org.readutf.orchestrator.shared.notification.impl.ServerRegisterNotification

class ServerRegisterListener(
    private val proxyServer: ProxyServer,
) : NotificationListener<ServerRegisterNotification> {
    override fun onNotification(notification: ServerRegisterNotification) {
        proxyServer.allPlayers // .filter { it.hasPermission("discordmc.alert.servercreate") }
            .forEach {
                it.sendMessage(
                    "&7[&9Orchestrator&7] &7Server &e${notification.server.getShortId()} &7has been registered!".toComponent(),
                )
            }
    }
}
