package ru.itis.androidsummer.net.client

import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import kotlin.concurrent.thread

class PlayerClient(val serverAddress: String, val port: Int = 1337) {
    private val thread = thread(start = false) { run() }
    private lateinit var socket: Socket
    private lateinit var outStream: DataOutputStream

    private fun run() {
        socket = Socket(serverAddress, port)
        outStream = DataOutputStream(socket.getOutputStream())
        DataInputStream(socket.getInputStream()).use { stream ->
            while (thread.isAlive && !thread.isInterrupted) {
                if (stream.available() > 0) {
                    val len = stream.readInt()
                    val packet = ByteArray(len)
                    stream.readFully(packet, 0, len)
                    parsePacket(packet)
                }
            }
        }
    }

    private fun parsePacket(bytes: ByteArray) =
        DataInputStream(ByteArrayInputStream(bytes)).use { stream ->
            val type = stream.readInt()
            val message = stream.readUTF()
            Log.d("network", "type: $type, message: $message")
        }

    private fun send(block: (stream: DataOutputStream) -> Unit) {
        val byteStream = ByteArrayOutputStream()
        DataOutputStream(byteStream).use(block)
        val bytes = byteStream.toByteArray()
        outStream.writeInt(bytes.size)
        outStream.write(bytes)
        outStream.flush()
    }

    fun sendMessage(message: String) = send {
        it.writeInt(0)
        it.writeUTF(message)
    }

    fun start() {
        thread.start()
    }
}
