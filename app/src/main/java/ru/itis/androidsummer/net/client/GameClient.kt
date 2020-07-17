package ru.itis.androidsummer.net.client

import ru.itis.androidsummer.net.NetPacket
import ru.itis.androidsummer.net.PacketCallback
import ru.itis.androidsummer.net.UdpWorker
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

abstract class GameClient(val port: Int = 1337) : UdpWorker() {
    override var socket = DatagramSocket()
    var address: InetAddress? = null
    private val buffer = ByteArray(1024)

    init {
        name = "client"
    }

    override fun run() {
        while (!isInterrupted) {
            try {
                val datagramPacket = DatagramPacket(buffer, buffer.size)
                socket.receive(datagramPacket)
                onDatagramReceived(datagramPacket)
            } catch (e: SocketException) {
                if (!isInterrupted) System.err.println("Client: ${e.message}")
            } catch (e: IOException) {
                e.printStackTrace(System.err)
            }
        }
    }

    fun NetPacket.send() = address?.let { sendPacket(this, it, port) }

    fun NetPacket.sendAwaiting(callback: PacketCallback) =
        address?.let { sendAwaitingPacket(this, it, port, callback) }

}
