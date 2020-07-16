package ru.itis.androidsummer

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_single_multi.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_IS_NOT_DEFAULT
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_QUESTION_PACK
import java.io.FileNotFoundException

class SingleMultiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_multi)

       firstViewWithTextForUser()

        var isSingle: Boolean

        btn_sm_choose_pack.setOnClickListener {
            packChooseVisibility()
            //
            bn_pack1.visibility = View.VISIBLE
            bn_pack3.visibility = View.VISIBLE
            bn_pack5.visibility = View.VISIBLE
            bn_pack6.visibility = View.VISIBLE
            //
        }

        btn_sm_user_isChoose.setOnClickListener {
            //TODO(загрузка паков(я тут хз как все устроено, оставляю вам))
           singleMultiChooseVisibility()
            //
            bn_pack1.visibility = View.INVISIBLE
            bn_pack3.visibility = View.INVISIBLE
            bn_pack5.visibility = View.INVISIBLE
            bn_pack6.visibility = View.INVISIBLE
            //
        }

        btn_sm_choose_multi.setOnClickListener {
            isSingle = false
            //TODO(сначала лобби с хостом и вот это все, сюда вставите, а потом переход в GameInterfaceActivity
            // с выбранными темами, но пока оставлю как ниже)
            val intent = Intent(this, GameInterfaceActivity::class.java)
            intent.putExtra("isSingle",isSingle)
            //TODO(поменять(Диляре желательно) putExtra и вот это все тут после добавления лобби и мультиплеера)
            startActivity(intent)
        }

        btn_sm_choose_single.setOnClickListener {
            btn_sm_easy.visibility = View.VISIBLE
            btn_sm_medium.visibility = View.VISIBLE
            btn_sm_hard.visibility = View.VISIBLE
            btn_sm_choose_single.visibility = View.INVISIBLE
            btn_sm_choose_multi.visibility = View.INVISIBLE
            iv_back_to_pack_choose.visibility = View.GONE
            iv_back_to_choose.visibility = View.GONE
            iv_back_to_menu.visibility = View.GONE
            btn_sm_user_isChoose.visibility = View.INVISIBLE
            iv_back_to_sm_choose.visibility = View.VISIBLE
            tv_sm_text_for_user.text = "Выбери сложность игры:"
            tv_sm_text_for_user.visibility = View.VISIBLE
            //TODO(добавить интенты в профиль для рейтинга(которого не будет здесь) и для счета/побед)
        }

        btn_sm_easy.setOnClickListener {
            isSingle = true
            val intent = Intent(this, GameInterfaceActivity::class.java)
            intent.putExtra("isSingle",isSingle)
            intent.putExtra("easy",0)
            startActivity(intent)
        }

        btn_sm_medium.setOnClickListener {
            isSingle = true
            val intent = Intent(this, GameInterfaceActivity::class.java)
            intent.putExtra("isSingle",isSingle)
            intent.putExtra("medium",1)
            startActivity(intent)
        }

        btn_sm_hard.setOnClickListener {
            isSingle = true
            val intent = Intent(this, GameInterfaceActivity::class.java)
            intent.putExtra("isSingle",isSingle)
            intent.putExtra("hard",2)
            startActivity(intent)
        }

        iv_back_to_menu.setOnClickListener {
            finish()
        }

        iv_back_to_choose.setOnClickListener {
            firstViewWithTextForUser()
        }

        iv_back_to_pack_choose.setOnClickListener {
            packChooseVisibility()
        }

        iv_back_to_sm_choose.setOnClickListener {
            btn_sm_easy.visibility = View.INVISIBLE
            btn_sm_medium.visibility = View.INVISIBLE
            btn_sm_hard.visibility = View.INVISIBLE
            iv_back_to_sm_choose.visibility = View.INVISIBLE
            iv_back_to_pack_choose.visibility = View.VISIBLE
            singleMultiChooseVisibility()
        }

        //мое
        val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        bn_pack1.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack1_file)).putBoolean(
                APP_PREFERENCES_IS_NOT_DEFAULT,false).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                    resources.getString(R.string.pack_setting_text_pack1),Toast.LENGTH_LONG).show()
        }
        bn_pack3.setOnClickListener {
            prefs.edit().putString(APP_PREFERENCES_QUESTION_PACK,
                resources.getString(R.string.pack_setting_text_pack3_file)).putBoolean(
                APP_PREFERENCES_IS_NOT_DEFAULT,false).apply()
            Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                    resources.getString(R.string.pack_setting_text_pack3),Toast.LENGTH_LONG).show()
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
            if (resultCode === Activity.RESULT_OK ) {
                val externalFileUri= data?.data.toString()
                val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(APP_PREFERENCES_IS_NOT_DEFAULT,true)
                    .putString(APP_PREFERENCES_QUESTION_PACK,externalFileUri).apply()
                Toast.makeText(this,resources.getString(R.string.pack_setting_text_you_have_set_a_pack) +
                        resources.getString(R.string.pack_setting_text_custom_notification),Toast.LENGTH_LONG).show()
            } else {
                throw FileNotFoundException()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        private const val REQUEST_CODE = 9999
    }

    fun firstViewWithTextForUser(){
        tv_sm_text_for_user.visibility = View.VISIBLE
        btn_sm_choose_pack.visibility = View.VISIBLE
        btn_sm_user_isChoose.visibility = View.INVISIBLE
        iv_back_to_menu.visibility = View.VISIBLE
        iv_back_to_choose.visibility = View.INVISIBLE
        iv_back_to_pack_choose.visibility = View.INVISIBLE
        tv_sm_text_for_user.text = "Сначала тебе нужно выбрать пакет с категориями для игры\n" +
                "    в зависимости от того, что ты предпочитаешь, и загрузить"
        //
        bn_pack1.visibility = View.INVISIBLE
        bn_pack3.visibility = View.INVISIBLE
        bn_pack5.visibility = View.INVISIBLE
        bn_pack6.visibility = View.INVISIBLE
    }
    fun packChooseVisibility(){
        tv_sm_text_for_user.visibility = View.INVISIBLE
        btn_sm_choose_pack.visibility = View.INVISIBLE
//            TODO("здесь делайте видимыми категорий паков. я думаю рекуклером и добавить CheckBox'ов,
//             после чего юзер выбирает(нужна проверка что только 1 пак) и нажимает кнопочку "Готово"")
        //кнопку btn_sm_user_isChoose в рекуклер не суйте только
        btn_sm_user_isChoose.visibility = View.VISIBLE
        iv_back_to_menu.visibility = View.GONE
        btn_sm_choose_single.visibility = View.INVISIBLE
        btn_sm_choose_multi.visibility = View.INVISIBLE
        iv_back_to_pack_choose.visibility = View.INVISIBLE
        iv_back_to_choose.visibility = View.VISIBLE
        //
        bn_pack1.visibility = View.VISIBLE
        bn_pack3.visibility = View.VISIBLE
        bn_pack5.visibility = View.VISIBLE
        bn_pack6.visibility = View.VISIBLE
    }

    fun singleMultiChooseVisibility(){
        btn_sm_choose_single.visibility = View.VISIBLE
        btn_sm_choose_multi.visibility = View.VISIBLE
        iv_back_to_pack_choose.visibility = View.VISIBLE
        iv_back_to_choose.visibility = View.GONE
        btn_sm_user_isChoose.visibility = View.INVISIBLE
        tv_sm_text_for_user.visibility = View.INVISIBLE
    }

}

