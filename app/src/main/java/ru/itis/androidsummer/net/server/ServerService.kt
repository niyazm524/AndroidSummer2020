package ru.itis.androidsummer.net.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ServerService : Service() {
    private lateinit var gameServer: GameServer

    override fun onCreate() {
        gameServer = OIGameServer { msg ->
            Log.i("service", "Service: $msg")
        }
        gameServer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameServer.recycle()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
