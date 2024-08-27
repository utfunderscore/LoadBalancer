package org.readutf.dev

import kotlinx.coroutines.runBlocking
import org.readutf.orchestrator.wrapper.OrchestratorApi
import java.net.InetAddress

fun main(args: Array<String>) {
    runBlocking {
        val orchestratorApi = OrchestratorApi(System.getenv("host"), 9393)

        val hostname = InetAddress.getLocalHost().getHostName()
        println(hostname)
        println(orchestratorApi.getPort(hostname))
    }
}
