package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_game_interface.*
import org.xmlpull.v1.XmlPullParser
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.MainActivity.Companion.APP_PREFERENCES_SCORE
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.concurrent.timer

class GameInterface : AppCompatActivity() {
    private val questionsAdapter = QuestionsAdapter()
    var rvAnswer:String? = null
    var rvQuestion:String? = null
    var rvPrice:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)

        //that's necessary
        val hashMap = HashMap<String,TreeMap<Int,HashSet<List<String>>>>()
        parse(hashMap)
        //don't delete

        getPack()
        rv_questions.apply {
            layoutManager =
                GridLayoutManager(this@GameInterface, questionsAdapter.getCategorySize() + 1)
            adapter = questionsAdapter
        }

        questionsAdapter.setOnItemClickListener {
                question ->  Toast.makeText(this,"$question",Toast.LENGTH_SHORT).show()
            rvAnswer = question.answer
            rvQuestion = question.question
            rvPrice = question.price
        }

        //temporary strict category and price!!
        val  category = "sport"
        val price = 100
        var case: List<String> =  hashMap.get(category)?.get(price)?.random() ?:  ArrayList<String>()
        val answer: EditText = findViewById(R.id.enterAnswer)
        case.drop(1)
        val prefs = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
        val score =  prefs.getInt(APP_PREFERENCES_SCORE,0)
        val me =  prefs.getString(APP_PREFERENCES_REGISTRATION,resources.getString(R.string.profile_text_default_name))
        val scopeView = findViewById<TextView>(R.id.count)
        val folksView = findViewById<TextView>(R.id.people)
        scopeView.text = score.toString()
        folksView.text = me
        //i'll fix it later as soon as Temur will have his table

        var heClick = false
        var hefinallClick:Boolean = false
        val bar: ProgressBar = findViewById(R.id.progressBar)

        wantAnswer.setOnClickListener {
            heClick = true
        }


        val time2 = object : CountDownTimer(20000,1000) {
            override fun onFinish() {
                //TODO : Возвращение к таблице с вопросами или хз че
                if(!hefinallClick or (bar.progress == 0)) {
                    timer2.text = "Вы не успели ввести ответ!"
                    Toast.makeText(this@GameInterface, "-$rvPrice очков!", Toast.LENGTH_SHORT).show()
                    prefs.edit().putInt(APP_PREFERENCES_SCORE,score - rvPrice).apply()
                    cancel()
                    //startActivity(Intent(this@GameInterface, MainMenu::class.java))
                } else {
                    Toast.makeText(this@GameInterface, "+$rvPrice очков!", Toast.LENGTH_SHORT).show()
                    prefs.edit().putInt(APP_PREFERENCES_SCORE,score + rvPrice).apply()
                    cancel()
                    //startActivity(Intent(this@GameInterface, MainMenu::class.java))
                }

            }
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timer2.text = "Осталось времени:" + millisUntilFinished / 1000
                var progress = (millisUntilFinished/1000).toInt()
                bar.progress = (bar.max - progress)
                if(hefinallClick) {
                    onFinish()
                }
            }

        }

        finallAnswer.setOnClickListener{
            hefinallClick = (answer.text.toString() == rvAnswer)
            time2.onFinish()
        }


        val time = object : CountDownTimer(20000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.text = "Осталось времени:" + millisUntilFinished / 1000
                if(heClick) {
//                    time2.start()
                    count.visibility = View.VISIBLE
                    numberOfRound.visibility = View.VISIBLE
                    wantAnswer.visibility = View.INVISIBLE
                    timer.visibility = View.INVISIBLE
                    enterAnswer.visibility = View.VISIBLE
                    finallAnswer.visibility = View.VISIBLE
                    people.visibility = View.VISIBLE
                    timer2.visibility = View.VISIBLE
                    onFinish()
                    time2.start()
                    cancel()
                }
                val progress = (millisUntilFinished/1000).toInt()
                bar.progress = (bar.max - progress)
            }

            override fun onFinish() {
                if(!heClick or (bar.progress == 0)) {
                    timer.text = "Время вышло!"
                }
            }
        }
        time.start()

    }

    //don't ye dare touch it!!
    private fun parse(hashMap: HashMap<String, TreeMap<Int, HashSet<List<String>>>>){
        try {
            val parser: XmlPullParser = resources.getXml(R.xml.quizes)
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG
                    && parser.name == "quiz"
                ) {
                    var category = ""
                    var price: Int = 0
                    var array = ArrayList<String>(6)
                    while (parser.eventType != XmlPullParser.END_TAG || parser.name != "quiz") {
                        if (parser.eventType == XmlPullParser.START_TAG){
                            when(parser.name){
                                "category" -> category = parser.getAttributeValue(0).toLowerCase()
                                "price" -> price = parser.getAttributeValue(0).toInt()
                                "question_itself" -> array.add(parser.getAttributeValue(0))
                                "right_variant" -> {
                                    for (i: Int in 0..(parser.attributeCount - 1)) {
                                        array.add(parser.getAttributeValue(i).toLowerCase())
                                    }
                                }
                            }
                        }
                        parser.next()
                    }
                    if(hashMap.containsKey(category)){
                        if (hashMap.get(category)!!.contains(price)){
                            hashMap.get(category)!!.get(price)!!.add(array)
                        }else{
                            val hashSet = HashSet<List<String>>()
                            hashSet.add(array)
                            hashMap.get(category)!!.put(price,hashSet)
                        }
                    } else{
                        val hashSet = HashSet<List<String>>()
                        hashSet.add(array)
                        val treeMap = TreeMap<Int,HashSet<List<String>>>()
                        treeMap.put(price,hashSet)
                        hashMap.put(category,treeMap)
                    }

                }
                parser.next()
            }
        } catch (t: Throwable) {
            Toast.makeText(this, t.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun getPack() {
        questionsAdapter.inputList(getCategoryList())

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

        categories.add(Category("Сора", questionList))



        return categories
    }
}