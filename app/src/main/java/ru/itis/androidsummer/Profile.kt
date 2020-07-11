package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.android.synthetic.main.profile.*
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_SCORE

class Profile : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
//        val signOut = findViewById<Button>(R.id.profile_button_sign_out)
//        val resetScore = findViewById<Button>(R.id.profile_button_reset_score)
//        val name = findViewById<TextView>(R.id.profile_text_name)
//        val score = findViewById<TextView>(R.id.profile_text_score)
        //лишнее и путает, нафег надо

        profile_text_name.text = "Твое имя: "+ sharedPreferences.getString(APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        )
        profile_text_score.text = "Текущее количество очков:"+ sharedPreferences.getInt(APP_PREFERENCES_SCORE,
            0
        ).toString()
        profile_button_sign_out.setOnClickListener {
            sharedPreferences.edit().remove(APP_PREFERENCES_REGISTRATION).apply()
            startActivity(Intent(this, Registration::class.java))
            finish()
        }
        profile_button_reset_score.setOnClickListener {
            sharedPreferences.edit().putInt(APP_PREFERENCES_SCORE,0).apply()
            profile_button_reset_score.text = "0"
            Toast.makeText(applicationContext, R.string.profile_button_score_reset_notification,
                Toast.LENGTH_LONG).show()
            finish()
        }





    }
}