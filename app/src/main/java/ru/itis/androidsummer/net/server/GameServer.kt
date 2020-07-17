package ru.itis.androidsummer.net.server

import ru.itis.androidsummer.net.UdpWorker
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException

abstract class GameServer(port: Int = 3443) : UdpWorker() {
    override var socket = DatagramSocket(port)

    init {
        name = "server"
    }

    override fun run() {
        while (!isInterrupted) {
            try {
                val data = ByteArray(1024)
                val datagramPacket = DatagramPacket(data, data.size)
                socket.receive(datagramPacket)
                onDatagramReceived(datagramPacket)
            } catch (e: SocketException) {
                if (!isInterrupted) System.err.println("Client: ${e.message}")
            } catch (e: IOException) {
                e.printStackTrace(System.err)
            }
        }
    }
}
