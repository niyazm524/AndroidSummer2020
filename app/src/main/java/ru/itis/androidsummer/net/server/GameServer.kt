package ru.itis.androidsummer.net.server

import java.net.ServerSocket
import kotlin.concurrent.thread

class GameServer : Thread() {
    private var isRunning = true
    private val serverSocket: ServerSocket = ServerSocket(1337)
    private val clients = mutableListOf<GameClient>()

    override fun run() {
        while (isRunning) {
            val client = serverSocket.accept()
            val gameClient = GameClient(this, client)
            clients.add(gameClient)
            gameClient.start()
        }
    }

    fun stopServer() {
        isRunning = false
        clients.forEach { it.stop() }
        this.interrupt()
    }
}
