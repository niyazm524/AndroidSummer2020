package ru.itis.androidsummer.net

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adroitandroid.near.connect.NearConnect
import com.adroitandroid.near.discovery.NearDiscovery
import com.adroitandroid.near.model.Host
import kotlinx.android.synthetic.main.activity_lobby.*
import ru.itis.androidsummer.R
import ru.itis.androidsummer.SplashActivity
import ru.itis.androidsummer.data.Server
import ru.itis.androidsummer.net.server.ServerService


class LobbyActivity : AppCompatActivity() {
    private val currentServerPort:String = ""
    private val currentServerId:Long = 0
    private lateinit var mNearDiscovery:NearDiscovery
    private lateinit var mNearConnect:NearConnect
    private val playersAdapter = PlayersAdapter()
    private val serversAdapter = ServersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        val prefs = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        val me = prefs.getString(
            SplashActivity.APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        ) ?: "Unknown"

        mNearDiscovery = NearDiscovery.Builder()
            .setContext(this)
            .setDiscoverableTimeoutMillis(50000)
            .setDiscoveryTimeoutMillis(50000)
            .setDiscoverablePingIntervalMillis(5000)
            .setDiscoveryListener(getNearDiscoveryListener(), Looper.getMainLooper())
            .setPort(1337) // optional
            .build()

        initDiscovery()


        rv_playerList.apply {
            layoutManager =
                LinearLayoutManager(this@LobbyActivity, playersAdapter.itemCount,false)
            adapter = playersAdapter
        }

        rv_serverList.apply{
            layoutManager =
                LinearLayoutManager(this@LobbyActivity, serversAdapter.itemCount,false)
            adapter = serversAdapter
        }


        srl_servers.setOnRefreshListener {
            if(mNearDiscovery.isDiscovering) {
                mNearDiscovery.stopDiscovery()
            }
            srl_servers.isRefreshing = true
            mNearDiscovery.startDiscovery()
        }
        serversAdapter.setOnItemClickListener {
            
        }

        playersAdapter.setOnItemClickListener {

        }

        btn_connect.setOnClickListener {

        }

        btn_host.setOnClickListener {
            startService(Intent(this, ServerService::class.java))
            mNearDiscovery.makeDiscoverable(me)
            Toast.makeText(this, "Теперь ты хостер", Toast.LENGTH_SHORT).show()
        }

        btn_startgame.setOnClickListener {

        }

    }

    private fun initDiscovery() {
        Toast.makeText(this,"Start Init",Toast.LENGTH_SHORT).show()
        mNearDiscovery.startDiscovery()
        srl_servers.isRefreshing = true
        mNearConnect.startReceiving()
        Log.d("ServerPart","${mNearDiscovery.allAvailablePeers}")
        Log.d("ServerPart","${mNearConnect.peers}")

    }

    override fun onStop() {
        super.onStop()
        mNearDiscovery.stopDiscovery()
        mNearDiscovery.makeNonDiscoverable()
        mNearConnect.stopReceiving(true)
    }

    private fun getNearConnectListener(): NearConnect.Listener {
            return object:NearConnect.Listener{
                override fun onReceive(bytes: ByteArray, sender: Host) {
                    Toast.makeText(this@LobbyActivity,"onReceive: ${sender.hostAddress}",Toast.LENGTH_SHORT).show()
                }

                override fun onSendComplete(jobId: Long) {
                    Toast.makeText(this@LobbyActivity,"onSendComplete: $jobId",Toast.LENGTH_SHORT).show()
                }

                override fun onSendFailure(e: Throwable?, jobId: Long) {
                    Toast.makeText(this@LobbyActivity,"onSendFailure: (${e?.message ?: "SendFailure"})  ($jobId)",Toast.LENGTH_SHORT).show()
                }

                override fun onStartListenFailure(e: Throwable?) {
                    Toast.makeText(this@LobbyActivity,"nStartListenFailure: ${e?.message ?: "ListenFailure"}",Toast.LENGTH_SHORT).show()
                }

            }
    }


    @NonNull
    private fun getNearDiscoveryListener() : NearDiscovery.Listener{
         return object:NearDiscovery.Listener{
             override fun onDiscoverableTimeout() {
                 Toast.makeText(this@LobbyActivity,"DiscoverableTimeout",Toast.LENGTH_SHORT).show()
             }

             override fun onDiscoveryFailure(e: Throwable) {
                 srl_servers.isRefreshing = false
                 Toast.makeText(this@LobbyActivity,"DiscoveryFailure: ${e.message}",Toast.LENGTH_SHORT).show()
             }

             override fun onDiscoveryTimeout() {
                 srl_servers.isRefreshing = false
                 Toast.makeText(this@LobbyActivity,"DiscoveryTimeout",Toast.LENGTH_SHORT).show()
             }

             override fun onPeersUpdate(hosts: Set<Host>) {
                 serversAdapter.submitList(
                     hosts.toList().map { host ->
                         Server(name = host.name, address = host.hostAddress, id = host.hostAddress.hashCode().toLong())
                     }
                 )
                 Toast.makeText(this@LobbyActivity,"PeersUpdate : ${hosts.toString()}",Toast.LENGTH_SHORT).show()
             }


         }
    }

}
