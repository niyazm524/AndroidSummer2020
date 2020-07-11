package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_SCORE

class ProfileActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)

        tv_profile_name.text = "Твое имя: "+ sharedPreferences.getString(APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        )
        tv_profile_score.text = "Текущее количество очков:"+ sharedPreferences.getInt(APP_PREFERENCES_SCORE,
            0
        ).toString()
        btn_profile_sign_Out.setOnClickListener {
            sharedPreferences.edit().remove(APP_PREFERENCES_REGISTRATION).apply()
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
        btn_profile_reset_score.setOnClickListener {
            sharedPreferences.edit().putInt(APP_PREFERENCES_SCORE,0).apply()
            btn_profile_reset_score.text = "0"
            Toast.makeText(applicationContext, R.string.profile_button_score_reset_notification,
                Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
