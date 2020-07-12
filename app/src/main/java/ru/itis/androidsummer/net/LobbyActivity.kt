package ru.itis.androidsummer.net

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


 class LobbyActivity : AppCompatActivity() {
    private val currentServerPort:String = ""
    private val currentServerId:Long = 0
    private var mNearDiscovery:NearDiscovery? = null
    private var mNearConnect:NearConnect? = null

     companion object {
         const val SERVER_TAG: String = "ServerPart"
     }

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        val playersAdapter = PlayersAdapter()
        val serversAdapter = ServersAdapter()

        mNearDiscovery = NearDiscovery.Builder()
            .setContext(this)
            .setDiscoverableTimeoutMillis(50000)
            .setDiscoveryTimeoutMillis(50000)
            .setDiscoverablePingIntervalMillis(5000)
            .setDiscoveryListener(getNearDiscoveryListener(), Looper.getMainLooper())
            .setPort(1337) // optional
            .build()

         mNearConnect = NearConnect.Builder()
            .fromDiscovery(mNearDiscovery!!)
            .setContext(this)
            .setListener(getNearConnectListener(), Looper.getMainLooper())
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



        serversAdapter.setOnItemClickListener {

        }

        playersAdapter.setOnItemClickListener {

        }

        btn_connect.setOnClickListener {

        }

        btn_host.setOnClickListener {

        }

        btn_startgame.setOnClickListener {

        }

    }

    private fun initDiscovery(){
        Toast.makeText(this,"Start Init",Toast.LENGTH_SHORT).show()
        mNearDiscovery!!.startDiscovery()
        mNearConnect!!.startReceiving()
        Log.d(SERVER_TAG,"${mNearDiscovery!!.allAvailablePeers}")
        Log.d(SERVER_TAG,"${mNearConnect!!.peers}")

    }

    private fun getNearConnectListener(): NearConnect.Listener {
            return object:NearConnect.Listener{
                override fun onReceive(bytes: ByteArray, sender: Host) {
                    Log.d(SERVER_TAG,"onReceive: ${sender.hostAddress}")
                }

                override fun onSendComplete(jobId: Long) {
                    Log.d(SERVER_TAG,"onSendComplete: $jobId")
                }

                override fun onSendFailure(e: Throwable?, jobId: Long) {
                    Log.d(SERVER_TAG,"onSendFailure: (${e?.message ?: "SendFailure"}")
                }

                override fun onStartListenFailure(e: Throwable?) {
                    Log.d(SERVER_TAG,"nStartListenFailure: ${e?.message ?: "ListenFailure"}")
                }

            }
    }


    @NonNull
    private fun getNearDiscoveryListener() : NearDiscovery.Listener{
         return object:NearDiscovery.Listener{
             override fun onDiscoverableTimeout() {
                 Log.d(SERVER_TAG,"DiscoverableTimeout")
             }

             override fun onDiscoveryFailure(e: Throwable) {
                 Log.d(SERVER_TAG,"DiscoveryFailure: ${e.message}")
             }

             override fun onDiscoveryTimeout() {
                 Log.d(SERVER_TAG,"DiscoveryTimeout")
             }

             override fun onPeersUpdate(host: Set<Host>) {
                 Log.d(SERVER_TAG,"PeersUpdate : $host")
             }


         }
    }



 }