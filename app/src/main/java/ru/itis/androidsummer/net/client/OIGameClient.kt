package ru.itis.androidsummer.net.client

import android.util.Log
import ru.itis.androidsummer.net.LoginPacket
import ru.itis.androidsummer.net.LogonPacket
import ru.itis.androidsummer.net.NetPacket

class OIGameClient : GameClient(port = 1488) {
    override fun onPacket(packet: NetPacket, hasCallback: Boolean) {
        Log.e("client", "received packet: ${packet.type.name}")
    }

    fun login(player: String, onResult: (success: Boolean) -> Unit) =
        LoginPacket(player).sendAwaiting {
            if (it is LogonPacket) {
                onResult(it.success)
            }
        }
}
