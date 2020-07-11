package ru.itis.androidsummer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.main_menu.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        main_menu_button_start.setOnClickListener {
            startActivity(Intent(this, GameInterface::class.java))
        }
        main_menu_button_rules.setOnClickListener {
            startActivity(Intent(this, Help::class.java))
        }
        main_menu_button_settings.setOnClickListener {
            //startActivity(Intent(this, ?::class.java))
        }
        main_menu_button_profile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
    }
}
