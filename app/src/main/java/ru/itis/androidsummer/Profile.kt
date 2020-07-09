package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_SCORE

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
        val signOut = findViewById<Button>(R.id.profile_button_sign_out)
        val resetScore = findViewById<Button>(R.id.profile_button_reset_score)
        val name = findViewById<TextView>(R.id.profile_text_name)
        val score = findViewById<TextView>(R.id.profile_text_score)
        name.text = sharedPreferences.getString(APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        )
        score.text = sharedPreferences.getInt(APP_PREFERENCES_SCORE,
            0
        ).toString()
        signOut.setOnClickListener {
            sharedPreferences.edit().remove(APP_PREFERENCES_REGISTRATION).apply()
            startActivity(Intent(this, Registration::class.java))
        }
        resetScore.setOnClickListener {
            sharedPreferences.edit().putInt(APP_PREFERENCES_SCORE,0).apply()
            score.text = "0"
            Toast.makeText(applicationContext, R.string.profile_button_score_reset_notification,
                Toast.LENGTH_LONG).show()
        }





    }
}