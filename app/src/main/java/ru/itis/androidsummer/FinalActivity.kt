package ru.itis.androidsummer

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_final.*

class FinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPreferences = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        val theme = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE).getString("theme","Light")
        if(theme == "Dark"){
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)

        var user_score = intent.getIntExtra("user_score", 0)
        val bot_score = intent.getIntExtra("bot_score", 0)
        val level = intent.getIntExtra("level", 0)

        val prefs = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        var victory = prefs.getInt(SplashActivity.APP_PREFERENCES_VICTORY, 0)
        var wholeScore = prefs.getInt(SplashActivity.APP_PREFERENCES_WHOLE_SCORE, 0)

        tv_final_user_score.text = "Ты:\n\n\n$user_score"
        tv_final_bot_score.text = "Бот:\n\n\n$bot_score"

        tv_final_bot_score.underline()
        tv_final_user_score.underline()

        if (user_score > bot_score) {
            tv_final_text.text = "Победа!"
            when (level) {
                0 -> user_score = (user_score * 1.2).toInt()
                1 -> user_score = (user_score * 1.35).toInt()
                2 -> user_score = (user_score * 1.5).toInt()
            }
            wholeScore += user_score
            victory++
        }
        else if (user_score < bot_score) {
            tv_final_text.text = "Поражение!"
            when (level) {
                0 -> user_score = (user_score * 0.2).toInt()
                1 -> user_score = (user_score * 0.35).toInt()
                2 -> user_score = (user_score * 0.5).toInt()
            }
            wholeScore += user_score
        } else {
            tv_final_text.text = "Ничья"
            wholeScore += user_score
        }

        btn_final_game_over.setOnClickListener {
            finish()
            onBackPressed()
            startActivity(Intent(this,MainMenuActivity::class.java))
        }

        prefs.edit().putInt(SplashActivity.APP_PREFERENCES_WHOLE_SCORE, wholeScore).apply()
        prefs.edit().putInt(SplashActivity.APP_PREFERENCES_VICTORY, victory).apply()
    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onBackPressed() {
        val prefs = getSharedPreferences(SplashActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putInt(SplashActivity.APP_PREFERENCES_SCORE, 0).apply()
        startActivity(Intent(this, MainMenuActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        super.onBackPressed()
    }
}