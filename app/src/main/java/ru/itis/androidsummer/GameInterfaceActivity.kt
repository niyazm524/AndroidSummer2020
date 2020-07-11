package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_game_interface.*
import org.xmlpull.v1.XmlPullParser
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_SCORE
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class GameInterfaceActivity : AppCompatActivity() {

    private val questionsAdapter = QuestionsAdapter()
    var rvAnswer:String? = null
    var rvQuestion:String? = null
    var rvPrice:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)

        //that's necessary
        val hashMap = HashMap<String,TreeMap<Int,HashSet<List<String>>>>()
        //don't delete

        getPack()
        rv_questions.apply {
            layoutManager =
                GridLayoutManager(this@GameInterfaceActivity, questionsAdapter.getCategoryCount(),GridLayoutManager.HORIZONTAL,false)
            adapter = questionsAdapter
        }


        //temporary strict category and price!!
        val  category = "sport"
        val price = 100
        var countRound = 1
        var case: List<String> =  hashMap.get(category)?.get(price)?.random() ?:  ArrayList<String>()
        val answer: EditText = findViewById(R.id.et_enterAnswer)
        case.drop(1)
        val prefs = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
        val score =  prefs.getInt(APP_PREFERENCES_SCORE,0)
        val me =  prefs.getString(APP_PREFERENCES_REGISTRATION,resources.getString(R.string.profile_text_default_name)) + "(ты)"
//        val scopeView = findViewById<TextView>(R.id.count)
//        val folksView = findViewById<TextView>(R.id.people)
        tv_count.text = "Очки:$score"
        tv_people.text = "ИГРОКИ: \n$me"
        tv_numberOfRound.text = "Раунд:" + (countRound)
        tv_people.visibility = View.VISIBLE
        //i'll fix it later as soon as Temur will have his table

        var heClick = false
        var heFinalClick = false

        btn_wantAnswer.setOnClickListener {
            heClick = true
        }

        fun resetQuestion(){
            heClick = false
            heFinalClick = false
            rvAnswer = null
            rvQuestion = null
            rvPrice = 0
            et_enterAnswer.setText("")
        }


        val time2 = object : CountDownTimer(20000,1000) {
            override fun onFinish() {
                //TODO : Возвращение к таблице с вопросами или хз че
                if(!heFinalClick or (bar.progress == 0)) {
                    tv_timer2.text = "Вы не успели ввести ответ!"
                    Toast.makeText(this@GameInterfaceActivity, "-$rvPrice очков!", Toast.LENGTH_SHORT).show()
                    prefs.edit().putInt(APP_PREFERENCES_SCORE,score - rvPrice).apply()
                    cancel()
                    //startActivity(Intent(this@GameInterface, MainMenu::class.java))
                } else if(heFinalClick) {
                    Toast.makeText(this@GameInterfaceActivity, "+$rvPrice очков!", Toast.LENGTH_SHORT).show()
                    prefs.edit().putInt(APP_PREFERENCES_SCORE,score + rvPrice).apply()
                    cancel()
                    //startActivity(Intent(this@GameInterface, MainMenu::class.java))
                }
                resetQuestion()
                makeInvisibleAnswerPart()
                rv_questions.visibility = View.VISIBLE
                tv_count.visibility = View.VISIBLE
                tv_numberOfRound.visibility = View.VISIBLE
                countRound++
                tv_numberOfRound.text = "Раунд:" + (countRound)
                tv_people.visibility = View.VISIBLE

            }
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tv_timer2.text = "Осталось времени:" + millisUntilFinished / 1000
                var progress = (millisUntilFinished/1000).toInt()
                bar.progress = (bar.max - progress)
                if(heFinalClick) {
                    progressBar.progress = progressBar.max
                    cancel()
                    onFinish()
                }
            }

        }

        btn_finallAnswer.setOnClickListener{
            heFinalClick = (answer.text.toString() == rvAnswer)
            if (enterAnswer.text.isEmpty()) {
                Toast.makeText(this@GameInterface, "Вы не ввели ответ!\n-$rvPrice очков!", Toast.LENGTH_SHORT).show()
                //в будущем можно будет апгрейдить и не давать отвечать пока не введет ответ или что нибудь еще помимо тоста
            }else
            if(!heFinalClick) {
                Toast.makeText(this@GameInterface, "Неправильный ответ!\n-$rvPrice очков!", Toast.LENGTH_SHORT).show()
            }
            time2.onFinish()
            progressBar.visibility = View.INVISIBLE
            tv_timer2.visibility = View.INVISIBLE
//            timer.visibility = View.INVISIBLE
        }


        val time = object : CountDownTimer(20000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_timer.text = "Осталось времени:" + millisUntilFinished / 1000
                progressBar.progress = progressBar.max
                if(heClick) {
                    progressBar.progress = progressBar.max
//                    time2.start()
                    makeVisibleAnswerPart()
                    onFinish()
                    time2.start()
                    cancel()
                }
                val progress = (millisUntilFinished/1000).toInt()
                bar.progress = progress
            }

            override fun onFinish() {
                if(!heClick or (bar.progress == 0)) {
                    tv_timer.text = "Время вышло!"
                }
            }
        }
//        time.start()
        questionsAdapter.setOnItemClickListener {
                question ->  Toast.makeText(this,"$question",Toast.LENGTH_SHORT).show()
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
            tv_numberOfRound.text = "Раунд:" + (countRound)
            tv_people.visibility = View.INVISIBLE
        }


    }


    private fun makeVisibleAnswerPart(){
        tv_count.visibility = View.VISIBLE
        tv_numberOfRound.visibility = View.VISIBLE
        btn_wantAnswer.visibility = View.INVISIBLE
        tv_timer.visibility = View.INVISIBLE
        et_enterAnswer.visibility = View.VISIBLE
        btn_finallAnswer.visibility = View.VISIBLE
        tv_people.visibility = View.VISIBLE
        tv_timer2.visibility = View.VISIBLE
    }

    private fun makeInvisibleAnswerPart(){
        tv_count.visibility = View.INVISIBLE
        tv_numberOfRound.visibility = View.INVISIBLE
//        timer.visibility = View.VISIBLE
        et_enterAnswer.visibility = View.INVISIBLE
        btn_finallAnswer.visibility = View.INVISIBLE
        tv_people.visibility = View.INVISIBLE
        tv_timer2.visibility = View.INVISIBLE
        tv_textquestion.visibility = View.INVISIBLE
    }


    //don't ye dare touch it!!
    private fun parseQuestion(): List<Category> {
        val categories =  ArrayList<Category>()
        try {
            val parser: XmlPullParser = resources.getXml(R.xml.quizes)
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "category") {
                    val category = parser.getAttributeValue(0)
                    categories.add(Category(category, ArrayList<Question>()))
                } else if (parser.eventType == XmlPullParser.START_TAG && parser.name == "quiz") {
                    var price: Int = 0
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
                    categories.last().transformIntoArray().add(Question(price,question,right))
                }
                parser.next()
            }
        } catch (t: Throwable) {
           Toast.makeText(this, t.toString(), Toast.LENGTH_LONG).show()
        }
        return categories
    }

    fun getPack() {
        questionsAdapter.inputList(parseQuestion())

    }

    fun getCategoryList(): List<Category> {
        var categories: ArrayList<Category> = ArrayList<Category>()
        var questionList: ArrayList<Question> = ArrayList<Question>()

        questionList.add(Question(100, "ИгрыВопрос1", "1"))
        questionList.add(Question(200, "ИгрыВопрос2", "1"))
        questionList.add(Question(300, "ИгрыВопрос3", "1"))
        questionList.add(Question(400, "ИгрыВопрос4", "1"))
        questionList.add(Question(500, "ИгрыВопрос5", "1"))


        categories.add(Category("Game", questionList))
        questionList = ArrayList<Question>()

        questionList.add(Question(150, "АнимеВопрос1", "1"))
        questionList.add(Question(250, "АнимеВопрос2", "1"))
        questionList.add(Question(350, "АнимеВопрос3", "1"))
        questionList.add(Question(450, "АнимеВопрос4", "1"))
        questionList.add(Question(550, "АнимеВопрос5", "1"))

        categories.add(Category("Anime", questionList))
        questionList = ArrayList<Question>()

        questionList.add(Question(120, "ФильмыВопрос1", "1"))
        questionList.add(Question(220, "ФильмыВопрос2", "1"))
        questionList.add(Question(320, "ФильмыВопрос3", "1"))
        questionList.add(Question(420, "ФильмыВопрос4", "1"))
        questionList.add(Question(520, "ФильмыВопрос5", "1"))

        categories.add(Category("Фильмы", questionList))

        questionList = ArrayList<Question>()

        questionList.add(Question(170, "ЯзыкиВопрос1", "1"))
        questionList.add(Question(270, "ЯзыкиВопрос2", "1"))
        questionList.add(Question(370, "ЯзыкиВопрос3", "1"))
        questionList.add(Question(470, "ЯзыкиВопрос4", "1"))
        questionList.add(Question(570, "ЯзыкиВопрос5", "1"))

        categories.add(Category("book", questionList))



        return categories
    }
}
