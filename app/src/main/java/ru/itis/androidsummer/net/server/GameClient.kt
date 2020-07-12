package ru.itis.androidsummer.net.server

import android.util.Log
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.net.Socket
import kotlin.concurrent.thread

class GameClient(private val gameServer: GameServer, private val socket: Socket) {
    private val outputStream = socket.getOutputStream()
    private lateinit var thread: Thread

    fun start() {
        thread = thread {
            run()
        }
    }

    fun run() {
        val inputStream = socket.getInputStream()
        DataInputStream(inputStream).use { stream ->
            if(stream.available() > 0) {
                val len = stream.readInt()
                val arr =  ByteArray(len)
                stream.readFully(arr, 0, len)
                parsePacket(arr)
            }
        }
    }

    fun stop() {
        if(thread.isAlive) {
            thread.interrupt()
        }
    }

    private fun parsePacket(bytes: ByteArray) = DataInputStream(ByteArrayInputStream(bytes)).use { stream ->
        val type = stream.readInt()
        val message = stream.readUTF()
        Log.d("network", "type: $type, message: $message")
    }
}
