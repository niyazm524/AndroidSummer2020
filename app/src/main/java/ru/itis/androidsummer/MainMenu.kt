package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton


class MainMenu : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.main_menu)
        
        val startButton = findViewById<Button>(R.id.main_menu_button_start) 
        val profileButton = findViewById<Button>(R.id.main_menu_button_rating)
        val settingsButton = findViewById<ImageButton>(R.id.main_menu_button_settings)
        val helpButton = findViewById<ImageButton>(R.id.main_menu_button_help)
        startButton.setOnClickListener {
            startActivity(Intent(this, Game::class.java))
        }
        helpButton.setOnClickListener {
            //startActivity(Intent(this, Teamuretc))
        }
        settingsButton.setOnClickListener {
            //startActivity(Intent(this, SettingsActivity::class.java))
        }
        profileButton.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
    }
}

