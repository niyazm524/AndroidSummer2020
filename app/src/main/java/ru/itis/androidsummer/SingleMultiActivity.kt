package ru.itis.androidsummer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_single_multi.*
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_THE_LAST_USED_PACK
import java.io.FileNotFoundException

class SingleMultiActivity : AppCompatActivity() {
    private var selectedPack: GamePack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_multi)

        firstViewWithTextForUser()

        var isSingle: Boolean

        setIsReadyButtonDisabled(true)

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
            intent.putExtra("isSingle", isSingle)
            val pack = selectedPack ?: return@setOnClickListener
            if (pack is GamePack.CustomPack) {
                intent.putExtra("packUri", pack.fileOrUri)
            } else {
                intent.putExtra("packFilename", pack.fileOrUri)
            }
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

        btn_sm_easy.setOnClickListener { startGameForSingle(0) }

        btn_sm_medium.setOnClickListener { startGameForSingle(1) }

        btn_sm_hard.setOnClickListener { startGameForSingle(2) }

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

        bn_pack1.setOnClickListener { selectPack(GamePack.LimpGta) }
        bn_pack3.setOnClickListener { selectPack(GamePack.LimpFunny) }
        bn_pack5.setOnClickListener { selectPack(GamePack.LimpGames) }

        bn_pack6.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*"
                startActivityForResult(intent, Companion.REQUEST_CODE)
            } catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun startGameForSingle(level: Int) {
        val pack = selectedPack ?: return
        val gameIntent = Intent(this, GameInterfaceActivity::class.java)
        gameIntent.putExtra("isSingle", false)
        gameIntent.putExtra("level", level)
        if (pack is GamePack.CustomPack) {
            gameIntent.putExtra("packUri", pack.fileOrUri)
        } else {
            gameIntent.putExtra("packFilename", pack.fileOrUri)
        }
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (prefs.getString(APP_PREFERENCES_THE_LAST_USED_PACK,"") == pack.fileOrUri){
            gameIntent.putExtra("isUsedBefore",true)
        }else
            prefs.edit().putString(APP_PREFERENCES_THE_LAST_USED_PACK,pack.fileOrUri).apply()
        startActivity(gameIntent)
    }

    private fun selectPack(pack: GamePack?) {
        selectedPack = pack
        if (pack != null) {
            Toast.makeText(
                this,
                resources.getString(R.string.pack_setting_text_you_have_set_a_pack, pack.name),
                Toast.LENGTH_LONG
            ).show()
        }

        setIsReadyButtonDisabled(selectedPack == null)
    }

    private fun setIsReadyButtonDisabled(disabled: Boolean) {
        btn_sm_user_isChoose.isEnabled = !disabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            val uri = data?.data
            if (resultCode == Activity.RESULT_OK && uri != null) {
                selectPack(GamePack.CustomPack(uri))
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

        private sealed class GamePack(val fileOrUri: String, val name: String) {
            object LimpGta : GamePack("limpGTA.siq", "GTA")
            object LimpGames : GamePack("limpGames.siq", "Games")
            object LimpFunny : GamePack("limpFunny.siq", "Funny")
            class CustomPack(uri: Uri) : GamePack(uri.toString(), "Собственный")
        }
    }

    private fun firstViewWithTextForUser() {
        tv_sm_text_for_user.visibility = View.VISIBLE
        btn_sm_choose_pack.visibility = View.VISIBLE
        btn_sm_user_isChoose.visibility = View.INVISIBLE
        iv_back_to_menu.visibility = View.VISIBLE
        iv_back_to_choose.visibility = View.INVISIBLE
        iv_back_to_pack_choose.visibility = View.INVISIBLE
        tv_sm_text_for_user.text = """Сначала тебе нужно выбрать пакет с категориями для игры
                    в зависимости от того, что ты предпочитаешь, и загрузить""".trimIndent()
        //
        bn_pack1.visibility = View.INVISIBLE
        bn_pack3.visibility = View.INVISIBLE
        bn_pack5.visibility = View.INVISIBLE
        bn_pack6.visibility = View.INVISIBLE
    }

    private fun packChooseVisibility() {
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

    private fun singleMultiChooseVisibility() {
        btn_sm_choose_single.visibility = View.VISIBLE
        btn_sm_choose_multi.visibility = View.VISIBLE
        iv_back_to_pack_choose.visibility = View.VISIBLE
        iv_back_to_choose.visibility = View.GONE
        btn_sm_user_isChoose.visibility = View.INVISIBLE
        tv_sm_text_for_user.visibility = View.INVISIBLE
    }

}

