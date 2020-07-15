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
        var isSingle = false
        var isMulti = false

        btn_sm_choose_pack.setOnClickListener {
            tv_sm_text_for_user.visibility = View.INVISIBLE
            btn_sm_choose_pack.visibility = View.INVISIBLE
//            TODO("здесь делайте видимыми категорий паков. я думаю рекуклером и добавить CheckBox'ов,
//             после чего юзер выбирает(нужна проверка что только 1 пак) и нажимает кнопочку "Готово"")
            //кнопку btn_sm_user_isChoose в рекуклер не суйте только
            btn_sm_user_isChoose.visibility = View.VISIBLE
            iv_back_to_menu.visibility = View.GONE
            iv_back_to_pack_choose.visibility = View.GONE
            iv_back_to_choose.visibility = View.VISIBLE
        }

        btn_sm_user_isChoose.setOnClickListener {
            //TODO(загрузка паков(я тут хз как все устроено, оставляю вам))
            btn_sm_choose_single.visibility = View.VISIBLE
            btn_sm_choose_multi.visibility = View.VISIBLE
            iv_back_to_pack_choose.visibility = View.VISIBLE
            iv_back_to_choose.visibility = View.GONE
            iv_back_to_menu.visibility = View.GONE
            btn_sm_user_isChoose.visibility = View.INVISIBLE
        }

        btn_sm_choose_multi.setOnClickListener {
            isMulti = true
            //TODO(сначала лобби с хостом и вот это все, сюда вставите, а потом переход в GameInterfaceActivity
            // с выбранными темами, но пока оставлю как ниже)
            startActivity(Intent(this, GameInterfaceActivity::class.java))
        }

        btn_sm_choose_single.setOnClickListener {
            startActivity(Intent(this, GameInterfaceActivity::class.java))
            isSingle = true
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
            //TODO(делаем ваш рекуклер невидимым тоже)
        }

        iv_back_to_pack_choose.setOnClickListener {
            btn_sm_choose_single.visibility = View.INVISIBLE
            btn_sm_choose_multi.visibility = View.INVISIBLE
            iv_back_to_pack_choose.visibility = View.INVISIBLE
            btn_sm_user_isChoose.visibility = View.VISIBLE
            //TODO(рекуклер видимым)
            iv_back_to_choose.visibility = View.VISIBLE
        }
    }
}