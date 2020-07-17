package ru.itis.androidsummer.net

import android.util.Log
import ru.itis.androidsummer.PacketDecoder
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

typealias PacketCallback = (reply: NetPacket) -> Unit

abstract class UdpWorker : Thread() {
    abstract var socket: DatagramSocket
    private var lastPacketId = 0
    private val awaitingPacketsCallbacks = hashMapOf<Int, PacketCallback>()

    protected open fun sendPacket(packet: NetPacket, ipAddress: InetAddress, port: Int) {
        if (packet.id == 0) packet.id = ++lastPacketId
        val bytes = packet.toBytes()
        //packet.print("Sent")
        socket.send(DatagramPacket(bytes, bytes.size, ipAddress, port))
    }

    fun sendAwaitingPacket(
        packet: NetPacket,
        ipAddress: InetAddress,
        port: Int,
        callback: PacketCallback
    ) {
        packet.id = ++lastPacketId
        awaitingPacketsCallbacks[packet.id] = callback
        sendPacket(packet, ipAddress, port)
    }

    protected fun onDatagramReceived(datagramPacket: DatagramPacket) {
        val packet: NetPacket = PacketDecoder.fromBytes(datagramPacket.data)
        //packet.print("Received")
        packet.source = datagramPacket.address to datagramPacket.port
        var hasCallback = false
        awaitingPacketsCallbacks.remove(packet.replyingTo)?.also { callback ->
            hasCallback = true
            callback(packet)
        }
        onPacket(packet, hasCallback)
    }

    private fun NetPacket.print(s: String) {
        Log.w(
            "network",
            "> $name Packet $s #${this.id}: ${javaClass.simpleName}, replyingTo = $replyingTo"
        )

    }

    abstract fun onPacket(packet: NetPacket, hasCallback: Boolean)

    fun NetPacket.replyWith(netPacket: NetPacket) = this.source?.let {
        netPacket.replyingTo = this.id
        sendPacket(netPacket, it.first, it.second)
    }

    open fun recycle() {
        interrupt()
        socket.close()
    }


}
