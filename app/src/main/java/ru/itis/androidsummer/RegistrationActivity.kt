package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_registration.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_SCORE

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        btn_registration_correct.setOnClickListener {
            var correctName = false
            val user = et_registration_users_name.text.toString()
            if (user.isNotEmpty() and !user.contains(" ")) {
                edit.putString(APP_PREFERENCES_REGISTRATION, user).apply()
                correctName = true
            } else {
                Toast.makeText(this,"Введи корректное имя!",Toast.LENGTH_SHORT).show()
            }
            if (!sharedPreferences.contains(APP_PREFERENCES_SCORE)) {
                edit.putInt(APP_PREFERENCES_SCORE, 0).apply()
            }
            if(correctName) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.registration_text_hello) +
                            sharedPreferences.getString(
                                APP_PREFERENCES_REGISTRATION,
                                "Если видете это, то значит тут баг!"
                            ),
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, MainMenuActivity::class.java))
                finish()
            }
        }
    }
}
