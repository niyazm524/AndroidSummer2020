package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences =
            getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val name = sharedPreferences.getString(APP_PREFERENCES_REGISTRATION,null)
        if(!name.isNullOrEmpty()){
            startActivity(Intent(this, MainMenuActivity::class.java))
        } else{
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        finish()
    }

    companion object {
        const val APP_PREFERENCES = "settings"
        const val APP_PREFERENCES_REGISTRATION = "userName"
        const val APP_PREFERENCES_SCORE = "userScore"
        const val APP_PREFERENCES_VICTORY = "userVictory"
        const val APP_PREFERENCES_WHOLE_SCORE = "userWholeScore"
    }
}
