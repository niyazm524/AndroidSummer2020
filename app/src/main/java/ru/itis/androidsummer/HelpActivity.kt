package ru.itis.androidsummer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        iv_help_back_to_menu.setOnClickListener {
            startActivity(Intent(this,MainMenuActivity::class.java))
        }
    }
}