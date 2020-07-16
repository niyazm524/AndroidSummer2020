package ru.itis.androidsummer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pack_selecting.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_IS_NOT_DEFAULT
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_QUESTION_PACK
import java.io.FileNotFoundException
import java.lang.Exception


class PackChoosingActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pack_selecting)
        val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        bn_start.setOnClickListener {
            startActivity(Intent(this, GameInterfaceActivity::class.java))
        }
        bn_pack1.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack1_file)).putBoolean(
                APP_PREFERENCES_IS_NOT_DEFAULT,false).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack1),Toast.LENGTH_LONG).show()
        }
        bn_pack2.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack2_file)).putBoolean(
                APP_PREFERENCES_IS_NOT_DEFAULT,false).apply()
                //Toast.makeText(this,"Аниме нету",Toast.LENGTH_LONG).show()
                //Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                //resources.getString(R.string.pack_setting_text_pack2),Toast.LENGTH_LONG).show()
        }
        bn_pack3.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack3_file)).putBoolean(
                APP_PREFERENCES_IS_NOT_DEFAULT,false).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack3),Toast.LENGTH_LONG).show()
        }
        bn_pack4.setOnClickListener {
            Toast.makeText(this,"Аниме нету",Toast.LENGTH_LONG).show()
            /* prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack4_file)).putBoolean(
                APP_PREFERENCES_IS_NOT_DEFAULT,false).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack4),Toast.LENGTH_LONG).show()*/
        }
        bn_pack5.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack5_file)).putBoolean(
                APP_PREFERENCES_IS_NOT_DEFAULT,false).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                resources.getString(R.string.pack_setting_text_pack5),Toast.LENGTH_LONG).show()
        }
        bn_pack6.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*"
                startActivityForResult(intent, Companion.REQUEST_CODE)
            } catch(e: Exception){
                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === REQUEST_CODE) {
            if (resultCode === Activity.RESULT_OK) {
                 val externalFileUri= data?.data.toString()
                val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(APP_PREFERENCES_IS_NOT_DEFAULT,true)
                    .putString(APP_PREFERENCES_QUESTION_PACK,externalFileUri).apply()
            } else {
                throw FileNotFoundException()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val REQUEST_CODE = 9999

    }

}