package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPreferences = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        val theme = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE).getString("theme","Light")
        if(theme == "Dark"){
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }

        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main_menu)

        btn_main_menu_start.setOnClickListener {
            startActivity(Intent(this, SingleMultiActivity::class.java))
        }
        btn_main_menu_rules.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
        iv_main_menu_settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
        iv_main_menu_profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}

