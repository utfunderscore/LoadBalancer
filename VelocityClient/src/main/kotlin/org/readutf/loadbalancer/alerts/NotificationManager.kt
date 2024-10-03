package org.readutf.loadbalancer.alerts

import com.fasterxml.jackson.databind.ObjectMapper
import org.readutf.orchestrator.shared.notification.Notification
import org.readutf.orchestrator.wrapper.OrchestratorApi

class NotificationManager(
    objectMapper: ObjectMapper,
    orchestratorApi: OrchestratorApi,
) {
    private val notificationSocket = orchestratorApi.createNotificationSocket(objectMapper = objectMapper, listener = ::handleNotification)

    private val listeners = mutableMapOf<Class<out Notification>, MutableList<NotificationListener<out Notification>>>()

    inline fun <reified T : Notification> listen(notificationListener: NotificationListener<T>): NotificationManager =
        listen(T::class.java, notificationListener)

    fun <T : Notification> listen(
        listenerClass: Class<T>,
        notificationListener: NotificationListener<T>,
    ): NotificationManager {
        listeners.getOrPut(listenerClass) { mutableListOf() }.add(notificationListener)
        return this
    }

    fun handleNotification(notification: Notification) {
        listeners
            .getOrPut(notification::class.java) { mutableListOf() }
            .forEach { (it as NotificationListener<*>).genericNotification(notification) }
    }

    fun shutdown() {
        notificationSocket.close()
    }
}
