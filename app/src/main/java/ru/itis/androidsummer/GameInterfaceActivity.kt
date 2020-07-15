package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_game_interface.*
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_IS_NOT_DEFAULT
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_QUESTION_PACK
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_SCORE
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_VICTORY
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_WHOLE_SCORE
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.parsers.ContentsXmlParser
import ru.itis.androidsummer.parsers.SiqParser
import ru.itis.androidsummer.utils.ProjectUtils.Companion.pickRandomQuestions
import java.io.*


class GameInterfaceActivity : AppCompatActivity() {

    private val questionsAdapter = QuestionsAdapter()
    private val siqParser = SiqParser()
    private lateinit var contentsXmlParser: ContentsXmlParser

    var rvAnswer: String? = null
    var rvQuestion: String? = null
    var rvPrice: Int = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)

        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        contentsXmlParser = ContentsXmlParser(parser)
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        try {
            val categories = getPack( randomize = true)
            questionsAdapter.inputList(skipUnsupportedCategories(categories))

            rv_questions.apply {
                isNestedScrollingEnabled = false
                layoutManager =
                    GridLayoutManager(
                        this@GameInterfaceActivity,
                        questionsAdapter.getCategoryCount(),
                        GridLayoutManager.HORIZONTAL,
                        false
                    )
                adapter = questionsAdapter
            }
        } catch (e: Throwable) {
            when (e) {
                is XmlPullParserException ->
                    Toast.makeText(this, R.string.game_text_wrong_siq_exception, Toast.LENGTH_LONG)
                        .show()
                is FileNotFoundException ->
                    Toast.makeText(this, R.string.game_text_no_file_exception, Toast.LENGTH_LONG)
                        .show()
                is IndexOutOfBoundsException ->
                    Toast.makeText(this, R.string.game_text_wrong_xml_structure, Toast.LENGTH_LONG)
                        .show()
                else -> Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
                finish()
        }

        var countRound = 1
        var score = prefs.getInt(APP_PREFERENCES_SCORE, 0)
        var victory = prefs.getInt(APP_PREFERENCES_VICTORY,0)
        var wholeScore = prefs.getInt(APP_PREFERENCES_WHOLE_SCORE,0)
        val me = prefs.getString(
            APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        ) + "(ты)"
        tv_count.text = "Счет:$score"
        tv_people.text = "ИГРОКИ: \n$me"
        tv_numberOfRound.text = "Раунд:$countRound"
        tv_people.visibility = View.VISIBLE
        //i'll fix it later as soon as Temur will have his table

        var heClick = false
        var heFinalClick = false

        btn_wantAnswer.setOnClickListener {
            heClick = true
        }

        fun resetQuestion() {
            heClick = false
            heFinalClick = false
            rvAnswer = null
            rvQuestion = null
            rvPrice = 0
            et_enterAnswer.setText("")
        }


        val time2 = object : CountDownTimer(20000, 1000) {
            override fun onFinish() {
                if (progressBar.progress == 0) {
                    tv_timer2.text = "Вы не успели ввести ответ!"
                    Toast.makeText(
                        this@GameInterfaceActivity,
                        "Вы не успели ввести ответ!\n-$rvPrice очков!",
                        Toast.LENGTH_SHORT
                    ).show()
                    score -= rvPrice
                    prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                    tv_count.text = "Счет:$score"
                    cancel()
                } else if (heFinalClick) {
                    Toast.makeText(
                        this@GameInterfaceActivity,
                        "+$rvPrice очков!",
                        Toast.LENGTH_SHORT
                    ).show()
                    score += rvPrice
                    prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                    tv_count.text = "Счет:$score"
                    cancel()
                }
                resetQuestion()
                cancel()
                countRound++
                tv_numberOfRound.text = "Раунд:$countRound"
                makeInvisibleAnswerPart()
                //надо будет добавить что-то для вывода результатов когда вопросы заканчиваются
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tv_timer2.text = "Осталось времени:" + millisUntilFinished / 1000
                progressBar.progress = (millisUntilFinished / 1000).toInt()
                if (heFinalClick) {
                    progressBar.progress = progressBar.max
                    cancel()
                    onFinish()
                }
            }

        }

        btn_finallAnswer.setOnClickListener {
            heFinalClick = (et_enterAnswer.text.toString().toLowerCase().trim()
                    == rvAnswer.toString().toLowerCase().trim())
            if (et_enterAnswer.text.isEmpty()) {
                Toast.makeText(this, "Вы не ввели ответ!\n-$rvPrice очков!", Toast.LENGTH_SHORT)
                    .show()
                score -= rvPrice
                prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                //в будущем можно будет апгрейдить и не давать отвечать пока не введет ответ или что нибудь еще помимо тоста
            } else if (!heFinalClick) {
                Toast.makeText(
                    this,
                    "Неправильный ответ!\n-$rvPrice очков!",
                    Toast.LENGTH_SHORT
                ).show()
                score -= rvPrice
                prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
            }
            tv_count.text = "Счет:$score"
            time2.onFinish()
            progressBar.visibility = View.INVISIBLE
            tv_timer2.visibility = View.INVISIBLE
            //в конце игры, когда будет выявлен победитель, нужно в SP +1 игроку добавить, be like:
//            victory++
//            prefs.edit().putInt(APP_PREFERENCES_VICTORY, victory).apply()
        }


        val time = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_timer.text = "Осталось времени:" + millisUntilFinished / 1000
                progressBar.progress = progressBar.max
                if (heClick) {
                    progressBar.progress = progressBar.max
                    makeVisibleAnswerPart()
                    onFinish()
                    time2.start()
                    cancel()
                }
                progressBar.progress = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                if (!heClick or (progressBar.progress == 0)) {
                    tv_timer.text = "Время вышло!"
                    //надо будет что-то добавить когда сеть сделаем(чтобы не оставался в том же положении)
                }
            }
        }
        questionsAdapter.setOnItemClickListener { question ->
            Toast.makeText(this, "$question", Toast.LENGTH_SHORT).show()
            btn_wantAnswer.visibility = View.VISIBLE
            rv_questions.visibility = View.INVISIBLE
            tv_textquestion.visibility = View.VISIBLE
            rvAnswer = question.answer
            rvQuestion = question.question
            rvPrice = question.price
            tv_textquestion.text = rvQuestion
            time.start()
            progressBar.visibility = View.VISIBLE
            tv_timer.visibility = View.VISIBLE
            tv_numberOfRound.text = "Раунд:$countRound"
            tv_people.visibility = View.INVISIBLE
        }

        //когда игра закончится, нужно будет в  SP сохранить итоговый счет игрока за игру и в профиль, be like:
//        wholeScore+=score (score предварительно умножить на коэф)
//        prefs.edit().putInt(APP_PREFERENCES_WHOLE_SCORE, score).apply()
    }


    private fun makeVisibleAnswerPart() {
        tv_count.visibility = View.VISIBLE
        tv_numberOfRound.visibility = View.VISIBLE
        btn_wantAnswer.visibility = View.INVISIBLE
        tv_timer.visibility = View.INVISIBLE
        et_enterAnswer.visibility = View.VISIBLE
        btn_finallAnswer.visibility = View.VISIBLE
        tv_people.visibility = View.VISIBLE
        tv_timer2.visibility = View.VISIBLE
    }

    private fun makeInvisibleAnswerPart() {
        tv_count.visibility = View.VISIBLE
        tv_numberOfRound.visibility = View.VISIBLE
        et_enterAnswer.visibility = View.INVISIBLE
        btn_finallAnswer.visibility = View.INVISIBLE
        tv_people.visibility = View.VISIBLE
        tv_timer2.visibility = View.INVISIBLE
        tv_textquestion.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        rv_questions.visibility = View.VISIBLE
    }

    private fun getPack(randomize: Boolean = true): List<Category> {
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        lateinit var path: String
        var dataStream: InputStream
        if (prefs.getBoolean(APP_PREFERENCES_IS_NOT_DEFAULT,false)) {
            dataStream = prefs.getString(APP_PREFERENCES_QUESTION_PACK, null) ?.let {
                contentResolver.openInputStream(Uri.parse(it)) }
                ?: throw FileNotFoundException()
        }else {
            path = prefs.getString(APP_PREFERENCES_QUESTION_PACK, "limpGTA.siq")
                ?: throw FileNotFoundException()
            dataStream = assets.open(path)
        }
        val contentsBytes = siqParser.parseSiq(dataStream)
        dataStream.close()
        val stream = ByteArrayInputStream(contentsBytes)
        val categories = contentsXmlParser.parseQuestion(stream)
        stream.close()
        return if (randomize) {
            pickRandomQuestions(categories, 3, 4)
        } else {
            categories
        }
    }

    private fun skipUnsupportedCategories(categories: List<Category>) =
        categories.filter { category ->
            category.questions.size > 1
        }



    override fun onBackPressed() {
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putInt(APP_PREFERENCES_SCORE, 0).apply()
        startActivity(Intent(this, MainMenuActivity::class.java))
    }

}
