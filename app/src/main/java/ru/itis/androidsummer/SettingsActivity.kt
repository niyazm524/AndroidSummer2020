package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_WHOLE_SCORE


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val sharedPreferences = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        val score = sharedPreferences.getInt(APP_PREFERENCES_WHOLE_SCORE,0)

        btn_settings_sign_Out.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }

        btn_settings_reset_score.setOnClickListener {
            if(score!=0) {
                sharedPreferences.edit().putInt(APP_PREFERENCES_WHOLE_SCORE, 0).apply()
                Toast.makeText(
                    this, R.string.profile_button_score_reset_notification,
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,"У тебя уже 0 очков!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        iv_settings_back_to_menu.setOnClickListener {
            finish()
        }
        //когда сеть будет, отсюда можно что нибудь с хостами я хз
        //можно добавить смену на темную тему
    }
}