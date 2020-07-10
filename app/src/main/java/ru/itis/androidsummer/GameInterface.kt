package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game_interface.*
import org.xmlpull.v1.XmlPullParser
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class GameInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)

        //that's necessary
        val hashMap = HashMap<String,TreeMap<Int,HashSet<List<String>>>>()
        parse(hashMap)
        //don't delete

        //temporary strict category and price!!
        val  category = "sport"
        val price = 100
        var case: List<String> =  hashMap.get(category)?.get(price)?.random() ?:  ArrayList<String>()
        val question: TextView = findViewById(R.id.question)
        val answer: EditText = findViewById(R.id.enterAnswer)
        question.setText(case.get(0))
        case.drop(1)
        //i'll fix it later as soon as Temur will have his table


        var heClick = false
        var hefinallClick = false
        val bar: ProgressBar = findViewById(R.id.progressBar)



        finallAnswer.setOnClickListener{
            if (case.contains(answer.text.toString().toLowerCase())) {
                hefinallClick = true
                startActivity(Intent(this, MainMenu::class.java))
            } else{
                hefinallClick = false
                startActivity(Intent(this, MainMenu::class.java))
            }
        }

        wantAnswer.setOnClickListener {
            heClick = true
        }


        var x = price //надо будет удалить после того как Булатек сделает туду
        val time2 = object : CountDownTimer(20000,1000) {
            override fun onFinish() {
                if(!hefinallClick or (bar.progress == 0)) {
                    timer2.text = "Вы не успели ввести ответ!"
//                    TODO("Булатек, тут нужно количество вычитаемых/добавляемых очков указать в тост вместо икса")
                    Toast.makeText(this@GameInterface, "-$x очков!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@GameInterface, "+$x очков!", Toast.LENGTH_SHORT).show()
                }
            }
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timer2.text = "Осталось времени:" + millisUntilFinished / 1000
                var progress = (millisUntilFinished/1000).toInt()
                bar.progress = (bar.max - progress)
                if(hefinallClick) {
                    onFinish()
                    //cancel()
//                    TODO("Возвращение к таблице с вопросами или хз че ")
                }
            }

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
}