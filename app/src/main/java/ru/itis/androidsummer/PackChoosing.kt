package ru.itis.androidsummer

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_pack_choosing.*
import kotlinx.android.synthetic.main.activity_pack_selecting.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_QUESTION_PACK

class PackChoosing : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pack_selecting)
        val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        ps_button_pack.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack1_file)).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack1),Toast.LENGTH_LONG).show()
        }
        ps_button_pack2.setOnClickListener {
            //prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                //resources.getString(R.string.pack_setting_text_pack2_file)).apply()
                Toast.makeText(this,"Аниме нету",Toast.LENGTH_LONG).show()
                //Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                //resources.getString(R.string.pack_setting_text_pack2),Toast.LENGTH_LONG).show()
        }
        ps_button_pack3.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack3_file)).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack3),Toast.LENGTH_LONG).show()
        }
        ps_button_pack4.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack4_file)).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack4),Toast.LENGTH_LONG).show()
        }
        ps_button_pack5.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack5_file)).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack5),Toast.LENGTH_LONG).show()
        }
    }

}