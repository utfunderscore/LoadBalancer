package org.readutf.loadbalancer.utils

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

val serializer = LegacyComponentSerializer.legacy('&')

fun String.toComponent() = serializer.deserialize(this)
