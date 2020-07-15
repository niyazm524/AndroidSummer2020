package ru.itis.androidsummer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main_menu)


        btn_main_menu_start.setOnClickListener {
            startActivity(Intent(this, GameInterfaceActivity::class.java))
        }
        btn_main_menu_rules.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
        iv_main_menu_settings.setOnClickListener {
            /*
            Bulat version: startActivity(Intent(this, PackChoosing::class.java))
            Dilyara version: startActivity(Intent(this, SettingsActivity::class.java))
             */
            /* TODO: а как какать (решайте сами, но имхо можно в сеттингс активити добавить кнопку
                которая открывает активити с выбором пака, но ещё более имхо
                выбор пака должен быть непосредственно перед началом игры, и затем в
                GameInterface через putExtra в интент должно передаваться имя файла выбранного пака
            */

            //startActivity(Intent(this, ?::class.java))
        }
        iv_main_menu_profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}

