package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_game_interface.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_SCORE
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class GameInterfaceActivity : AppCompatActivity() {

    private val questionsAdapter = QuestionsAdapter()
    var rvAnswer: String? = null
    var rvQuestion: String? = null
    var rvPrice: Int = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)


        getPack()
        rv_questions.apply {
            layoutManager =
                GridLayoutManager(
                    this@GameInterfaceActivity,
                    questionsAdapter.getCategoryCount(),
                    GridLayoutManager.HORIZONTAL,
                    false
                )
            adapter = questionsAdapter
        }


        var countRound = 1
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val score = prefs.getInt(APP_PREFERENCES_SCORE, 0)
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

                //TODO : Возвращение к таблице с вопросами или хз че
                if  (progressBar.progress == 0) {

                    tv_timer2.text = "Вы не успели ввести ответ!"
                    Toast.makeText(
                        this@GameInterfaceActivity,
                        "Вы не успели ввести ответ!\n-$rvPrice очков!",
                        Toast.LENGTH_SHORT
                    ).show()
                    prefs.edit().putInt(APP_PREFERENCES_SCORE, score - rvPrice).apply()
                    cancel()
                } else if (heFinalClick) {
                    Toast.makeText(
                        this@GameInterfaceActivity,
                        "+$rvPrice очков!",
                        Toast.LENGTH_SHORT
                    ).show()
                    prefs.edit().putInt(APP_PREFERENCES_SCORE, score + rvPrice).apply()
                    cancel()
                }
                resetQuestion()
                cancel()
                makeInvisibleAnswerPart()
                rv_questions.visibility = View.VISIBLE
                tv_count.visibility = View.VISIBLE
                tv_numberOfRound.visibility = View.VISIBLE
                countRound++
                tv_numberOfRound.text = "Раунд:$countRound"
                tv_people.visibility = View.VISIBLE

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
            heFinalClick = (et_enterAnswer.text.toString() == rvAnswer)
            if (et_enterAnswer.text.isEmpty()) {
                Toast.makeText(this, "Вы не ввели ответ!\n-$rvPrice очков!", Toast.LENGTH_SHORT)
                    .show()
                //в будущем можно будет апгрейдить и не давать отвечать пока не введет ответ или что нибудь еще помимо тоста
            } else
                if (!heFinalClick) {
                    Toast.makeText(
                        this,
                        "Неправильный ответ!\n-$rvPrice очков!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            time2.onFinish()
            progressBar.visibility = View.INVISIBLE
            tv_timer2.visibility = View.INVISIBLE
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
        tv_count.visibility = View.INVISIBLE
        tv_numberOfRound.visibility = View.INVISIBLE
        et_enterAnswer.visibility = View.INVISIBLE
        btn_finallAnswer.visibility = View.INVISIBLE
        tv_people.visibility = View.INVISIBLE
        tv_timer2.visibility = View.INVISIBLE
        tv_textquestion.visibility = View.INVISIBLE
    }


    //don't ye dare touch it!!
    private fun parseQuestion(): List<Category> {
        val categories = ArrayList<Category>()
        try {
            val parser: XmlPullParser = resources.getXml(R.xml.quizes)
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "category") {
                    val category = parser.getAttributeValue(0)
                    categories.add(Category(category, ArrayList()))
                } else if (parser.eventType == XmlPullParser.START_TAG && parser.name == "quiz") {
                    var price = 0
                    var question = ""
                    var right = ""
                    while (parser.eventType != XmlPullParser.END_TAG || parser.name != "quiz") {
                        if (parser.eventType == XmlPullParser.START_TAG) {
                            when (parser.name) {
                                "price" -> price = parser.getAttributeValue(0).toInt()
                                "question_itself" -> question = parser.getAttributeValue(0)
                                "right_variant" -> right = parser.getAttributeValue(0)
                            }
                        }
                        parser.next()
                    }
                    categories.last().transformIntoArray().add(Question(price, question, right))
                }
                parser.next()
            }
        } catch (t: Throwable) {
            Toast.makeText(this, t.toString(), Toast.LENGTH_LONG).show()
        }
        return categories
    }

    fun getPack() {
        questionsAdapter.inputList(parseQuestion(parseSiq(assets.open("limp.siq"))))
    }
    private fun parseQuestion(parser: XmlPullParser): List<Category> {
        val categories =  ArrayList<Category>()
        var i = 0
        try {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "theme") {
                    val category = parser.getAttributeValue(0)
                    categories.add(Category(category, ArrayList<Question>()))
                    parser.next()
                } else if(parser.eventType == XmlPullParser.START_TAG && parser.name == "question") {
                    var price = 0
                    var question = ""
                    var right = ""
                    while (!(parser.eventType == XmlPullParser.END_TAG && parser.name == "question")) {
                        if(parser.eventType == XmlPullParser.START_TAG) {
                            if ( parser.name == "question") {
                                price = parser.getAttributeValue(0).toInt()
                                parser.next()
                            } else if (parser.name == "atom") {
                                //question = parser.getAttributeValue(0)
                                parser.next()
                                parser.text
                                question = parser.text
                            } else if (parser.name == "answer") {
                                //right = parser.getAttributeValue(0)
                                parser.next()
                                right = parser.text//.encodeToByteArray().contentToString()
                            }
                        }
                        parser.next()
                    }
                    categories.last().transformIntoArray().add(Question(price,question,right))
                    //Toast.makeText(this,price.toString()+question+ "|" + right,Toast.LENGTH_LONG).show()

                    //костыль, удалить при первой возможности!!
                    if (i<7)
                        i++
                    else
                        break
                } else
                    parser.next()
            }
        } catch (t: Throwable) {
            Toast.makeText(this, t.toString(), Toast.LENGTH_LONG).show()
        }
        return categories
    }
    fun parseSiq(file: InputStream): XmlPullParser {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        try {
            val stream = ZipInputStream(file)
            var zip = stream.nextEntry
            while (zip != null) {
                if(zip.name =="content.xml") {
                    val streamReader: InputStreamReader = stream.reader(Charset.forName("UTF-8"))
                    parser.setInput(streamReader)
                    /* var i = streamReader.read()
                     while(i  != -1){
                         string += i.toChar()
                         i = streamReader.read()
                         break
                     }*/
                    break
                }
                stream.closeEntry()
                zip = stream.nextEntry
            }
        } catch (t: Exception){
            Toast.makeText(this,t.toString(),Toast.LENGTH_LONG).show()
        }
        return parser
    }

}
