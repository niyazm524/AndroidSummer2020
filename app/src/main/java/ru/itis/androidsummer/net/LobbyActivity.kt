package ru.itis.androidsummer.net

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_lobby.*
import ru.itis.androidsummer.R

class LobbyActivity : AppCompatActivity() {
    private val playersAdapter = PlayersAdapter()
    private val serversAdapter = ServersAdapter()
    private val currentServerPort:String = ""
    private val currentServerId:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)


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
    }


}