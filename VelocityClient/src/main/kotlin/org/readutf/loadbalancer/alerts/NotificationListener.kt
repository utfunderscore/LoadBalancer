package org.readutf.loadbalancer.alerts

import org.readutf.orchestrator.shared.notification.Notification

fun interface NotificationListener<T : Notification> {
    fun onNotification(notification: T)

    fun genericNotification(notification: Notification) {
        onNotification(notification as T)
    }
}
