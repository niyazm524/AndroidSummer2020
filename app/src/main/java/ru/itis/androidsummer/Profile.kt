package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_REGISTRATION

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
        val signOut = findViewById<Button>(R.id.profile_button_sign_out)
        val name = findViewById<TextView>(R.id.profile_text_name)
        val score = findViewById<TextView>(R.id.profile_text_score)
        name.text = sharedPreferences.getString(APP_PREFERENCES_REGISTRATION,
            R.string.profile_text_default_name.toString()
        )
        signOut.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this, Registration::class.java))
        }
    }
}