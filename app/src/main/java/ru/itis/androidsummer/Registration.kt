package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_REGISTRATION

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)
        val sharedPreferences =
            getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val userName = findViewById<EditText>(R.id.registration_text_users_name)
        val button = findViewById<Button>(R.id.registration_button_correct)
        val edit = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE).edit()
        button.setOnClickListener{
            val user = userName.text
            edit.putString(APP_PREFERENCES_REGISTRATION, user.toString()).apply()
            Toast.makeText(applicationContext, "Hello, $user",Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainMenu::class.java))
        }
    }
}
