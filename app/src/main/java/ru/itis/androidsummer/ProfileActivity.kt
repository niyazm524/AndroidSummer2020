package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_WHOLE_SCORE
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_VICTORY

class ProfileActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val score = sharedPreferences.getInt(APP_PREFERENCES_WHOLE_SCORE, 0)

        tv_profile_name.text = sharedPreferences.getString(
            APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        )
        tv_profile_victory_count.text =
            sharedPreferences.getInt(APP_PREFERENCES_VICTORY, 0).toString()

        //нужно будет сохранять количество очков за игру(после завершения)
        // и выводить сюда(они будут постоянными, просто накапливаться), be like:
        if (score == 0) {
            tv_profile_score.text = sharedPreferences.getString(
                APP_PREFERENCES_WHOLE_SCORE,
                resources.getString(R.string.profile_text_default_score)
            )
        } else
            tv_profile_score.text = score.toString()

//        tv_profile_place_in_rating.text = //доставать откуда то место в рейтинге


//        tv_profile_name_victory.text = // из SP надо доставать колво побед и в профиль выводить(нужна сеть)

        tv_profile_name.underline()
        tv_profile_score.underline()
        tv_profile_victory_count.underline()
        tv_profile_place_in_rating.underline()

        iv_profile_back_to_menu.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
        }

        iv_profile_rating.setOnClickListener {
            //переход на страничку с рейтингом, которого пока нет
        }

    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}
