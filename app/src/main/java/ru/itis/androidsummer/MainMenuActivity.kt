package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenuActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main_menu)
        
//        val startButton = findViewById<Button>(R.id.main_menu_button_start)
//        val profileButton = findViewById<ImageButton>(R.id.main_menu_button_rules)
//        val settingsButton = findViewById<ImageButton>(R.id.main_menu_button_settings)
//        val helpButton = findViewById<Button>(R.id.main_menu_button_rules)
        //лишнее и путает, нафег надо

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
    }
}

