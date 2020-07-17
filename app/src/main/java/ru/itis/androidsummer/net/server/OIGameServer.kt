package ru.itis.androidsummer.net.server

import ru.itis.androidsummer.net.*
import ru.itis.androidsummer.net.data.NetPlayer

class OIGameServer(private val message: (msg: String) -> Unit = {}) : GameServer(1488) {
    private val players: MutableList<NetPlayer> = mutableListOf()
    override fun onPacket(packet: NetPacket, hasCallback: Boolean) = when (packet) {
        is LoginPacket -> onLogin(packet)
        is LogonPacket -> TODO()
        is EmptyPacket -> TODO()
    }

    private fun onLogin(loginPacket: LoginPacket) {
        players += NetPlayer(
            loginPacket.playerName,
            loginPacket.source!!.first,
            loginPacket.source!!.second
        )
        message("Player with name ${loginPacket.playerName} logon!")
        loginPacket.replyWith(LogonPacket(true))
    }

    private fun NetPlayer.sendPacket(packet: NetPacket) {
        super.sendPacket(packet, this.address, this.port)
    }

    private fun NetPlayer.sendAwaitingPacket(packet: NetPacket, callback: PacketCallback) {
        super.sendAwaitingPacket(packet, this.address, this.port, callback)
    }
}
