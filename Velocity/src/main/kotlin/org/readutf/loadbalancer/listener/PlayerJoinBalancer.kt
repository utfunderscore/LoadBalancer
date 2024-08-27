package org.readutf.loadbalancer.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import kotlinx.coroutines.runBlocking
import org.readutf.loadbalancer.balancer.LoadBalancer
import org.readutf.loadbalancer.utils.toComponent

class PlayerJoinBalancer(
    val loadBalancer: LoadBalancer,
) {
    @Subscribe
    fun onLogin(e: ServerPreConnectEvent) {
        if (e.previousServer != null) {
            return
        }

        runBlocking {
            val serverResult = loadBalancer.findBestServer("")

            if (serverResult.isError()) {
                e.player.disconnect("&cFailed to find a lobby.".toComponent())
            } else {
                val server = serverResult.get()
                e.result = ServerPreConnectEvent.ServerResult.allowed(server)
            }
        }
    }
}
