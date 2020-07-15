package ru.itis.androidsummer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main_menu)

        mm_star_button.setOnClickListener {
            startActivity(Intent(this, GameInterfaceActivity::class.java))
        }
        mm_help_button.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
        iv_main_menu_settings.setOnClickListener {
            startActivity(Intent(this, PackChoosing::class.java))
        }
        iv_main_menu_profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}

