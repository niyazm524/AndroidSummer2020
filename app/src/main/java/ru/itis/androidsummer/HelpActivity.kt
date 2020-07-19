package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPreferences = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        val theme = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE).getString("theme","Light")
        if(theme == "Dark"){
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        iv_help_back_to_menu.setOnClickListener {
            finish()
        }
    }
}