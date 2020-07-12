package ru.itis.androidsummer.net.server

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ServerService : Service() {
    private lateinit var gameServer: GameServer

    override fun onCreate() {
        gameServer = GameServer()
        gameServer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameServer.stopServer()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
