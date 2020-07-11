package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_SCORE

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)
        val userName = findViewById<EditText>(R.id.registration_text_users_name)
        val button = findViewById<Button>(R.id.registration_button_correct)
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        button.setOnClickListener{
            val user = userName.text
            if (user.toString()!="")
                edit.putString(APP_PREFERENCES_REGISTRATION, user.toString()).apply()
            else
                edit.putString(APP_PREFERENCES_REGISTRATION,
                    resources.getString(R.string.profile_text_default_name)).apply()
            if (!sharedPreferences.contains(APP_PREFERENCES_SCORE)) {
                edit.putInt(APP_PREFERENCES_SCORE, 0).apply()
            }
            Toast.makeText(applicationContext, resources.getString(R.string.registration_text_hello)
                    + sharedPreferences
                        .getString(APP_PREFERENCES_REGISTRATION,
                            resources.getString(R.string.profile_text_default_name)),
                                Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
