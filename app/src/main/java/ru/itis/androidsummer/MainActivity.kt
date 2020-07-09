package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        sharedPreferences =
            getSharedPreferences(Companion.APP_PREFERENCES, Context.MODE_PRIVATE)
        if(sharedPreferences.contains(Companion.APP_PREFERENCES_REGISTRATION)){
            startActivity(Intent(this, MainMenu::class.java))
        } else{
            startActivity(Intent(this, Registration::class.java))
        }
    }

    companion object {
        const val APP_PREFERENCES = "settings"
        const val APP_PREFERENCES_REGISTRATION = "userName"
    }
}
