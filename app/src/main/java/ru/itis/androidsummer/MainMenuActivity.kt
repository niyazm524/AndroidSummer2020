package ru.itis.androidsummer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*
import ru.itis.androidsummer.net.LobbyActivity


class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main_menu)

        btn_main_menu_start.setOnClickListener {
            startActivity(Intent(this, GameInterfaceActivity::class.java))
        }
        btn_main_menu_rules.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
        iv_main_menu_settings.setOnClickListener {
            //startActivity(Intent(this, ?::class.java))
        }
        iv_main_menu_profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }


        btn_toServer.setOnClickListener{
            val serverActivity = Intent(this,LobbyActivity::class.java)
            startActivity(serverActivity)
        }

    }
}

