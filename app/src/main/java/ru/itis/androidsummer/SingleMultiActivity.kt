package ru.itis.androidsummer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_single_multi.*

class SingleMultiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_multi)

        var isSingle: Boolean

        btn_sm_choose_pack.setOnClickListener {
            packChooseVisibility()
        }

        btn_sm_user_isChoose.setOnClickListener {
            //TODO(загрузка паков(я тут хз как все устроено, оставляю вам))
           singleMultiChooseVisibility()
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
            tv_sm_text_for_user.visibility = View.VISIBLE
            btn_sm_choose_pack.visibility = View.VISIBLE
            btn_sm_user_isChoose.visibility = View.INVISIBLE
            iv_back_to_menu.visibility = View.VISIBLE
            iv_back_to_choose.visibility = View.INVISIBLE
            iv_back_to_pack_choose.visibility = View.INVISIBLE
            //TODO(делаем ваш рекуклер c выбором паков невидимым тоже)
        }

        iv_back_to_pack_choose.setOnClickListener {
            btn_sm_choose_single.visibility = View.INVISIBLE
            btn_sm_choose_multi.visibility = View.INVISIBLE
            iv_back_to_pack_choose.visibility = View.INVISIBLE
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
    }

    fun packChooseVisibility(){
        tv_sm_text_for_user.visibility = View.INVISIBLE
        btn_sm_choose_pack.visibility = View.INVISIBLE
//            TODO("здесь делайте видимыми категорий паков. я думаю рекуклером и добавить CheckBox'ов,
//             после чего юзер выбирает(нужна проверка что только 1 пак) и нажимает кнопочку "Готово"")
        //кнопку btn_sm_user_isChoose в рекуклер не суйте только
        btn_sm_user_isChoose.visibility = View.VISIBLE
        iv_back_to_menu.visibility = View.GONE
        iv_back_to_choose.visibility = View.VISIBLE
    }

    fun singleMultiChooseVisibility(){
        btn_sm_choose_single.visibility = View.VISIBLE
        btn_sm_choose_multi.visibility = View.VISIBLE
        iv_back_to_pack_choose.visibility = View.VISIBLE
        iv_back_to_choose.visibility = View.GONE
        btn_sm_user_isChoose.visibility = View.INVISIBLE
    }
}