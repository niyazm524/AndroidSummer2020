package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.*
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_game_interface.*
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_REGISTRATION
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_SCORE
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_VICTORY
import ru.itis.androidsummer.SplashActivity.Companion.APP_PREFERENCES_WHOLE_SCORE
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.parsers.ContentsXmlParser
import ru.itis.androidsummer.parsers.SiqParser
import ru.itis.androidsummer.utils.ProjectUtils.Companion.pickRandomQuestions
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.InputStream


class GameInterfaceActivity : AppCompatActivity() {

    private val questionsAdapter = QuestionsAdapter()
    private val siqParser = SiqParser()
    private lateinit var contentsXmlParser: ContentsXmlParser

    var rvAnswer: String? = null
    var rvQuestion: String? = null
    var rvPrice: Int = 0
    var correctAnswer: Boolean = false
    var isChoose = false

    var heClick = false
    var heFinalClick = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)

        var isSingle = intent.getBooleanExtra("isSingle", false)
        //TODO(поменять(Диляре) тут после добавления лобби и мультиплеера)
        //TODO(Тимур, вот тут наподобие примера выше вытаскивый сложность, я ее передаю)


        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        contentsXmlParser = ContentsXmlParser(parser)
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        tv_textquestion.movementMethod = ScrollingMovementMethod()

        try {
            var isPackFromUri = true
            val pack: String = intent.getStringExtra("packUri")
                ?: (intent.getStringExtra("packFilename") ?: "limpGTA.siq").also {
                    isPackFromUri = false
                }
            val categories = getPack(pack, isPackFromUri, randomize = false)
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
        var victory = prefs.getInt(APP_PREFERENCES_VICTORY, 0)
        var wholeScore = prefs.getInt(APP_PREFERENCES_WHOLE_SCORE, 0)
        var countCharacter = 0
        var helpSymbolPrice = 100
        var helpBotPrice = 200
        val me = prefs.getString(
            APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        ) + "(ты)"
        tv_count.text = "Счет:$score"
        tv_people.text = "ИГРОКИ: \n$me"
        tv_numberOfRound.text = "Раунд:$countRound"
        if (!isSingle) {
            tv_people.visibility = View.VISIBLE
            Toast.makeText(this, "Вы выбрали игру с друзьями!", Toast.LENGTH_SHORT).show()
        }
        if (isSingle) Toast.makeText(this, "Вы выбрали одиночную игру! Уровень:", Toast.LENGTH_SHORT).show()
        //тут конечно теперь пустовато на экране с таблицей для одиночки, надо будет подумать над этим
        //TODO(сюда в тост вставь сложность после "уровень:")

        iv_getOneChar.setOnClickListener {
            Toast.makeText(applicationContext, "Попытка купли", Toast.LENGTH_SHORT).show()
            val checkScore = prefs.getInt(APP_PREFERENCES_WHOLE_SCORE, 0)
            if(checkScore >= helpSymbolPrice){
                Toast.makeText(applicationContext, "Символ покупка", Toast.LENGTH_SHORT).show()
                countCharacter++
                    if(countCharacter <= (rvAnswer?.lastIndex ?: 0)+1){
                        prefs.edit().putInt(APP_PREFERENCES_WHOLE_SCORE,checkScore-helpSymbolPrice).apply()
                    et_enterAnswer.setText(rvAnswer?.subSequence(0,countCharacter))
                }
                else{
                    Toast.makeText(applicationContext, "Все символы есть", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Недостаточно баллов, текущее количество:$checkScore", Toast.LENGTH_SHORT).show()
            }
        }

        iv_getOneChar.setOnLongClickListener{
            Toast.makeText(applicationContext, "Один символ из ответа", Toast.LENGTH_SHORT).show()
            true
        }

        iv_helpCallBot.setOnClickListener {
            Toast.makeText(applicationContext, "Попытка звонка", Toast.LENGTH_SHORT).show()
            val checkScore = prefs.getInt(APP_PREFERENCES_WHOLE_SCORE, 0)
            if(checkScore >= helpBotPrice){
                prefs.edit().putInt(APP_PREFERENCES_WHOLE_SCORE,checkScore-helpBotPrice).apply()
                Toast.makeText(applicationContext, "Бот звонок", Toast.LENGTH_SHORT).show()
                //TODO метод тип 1 к 2
            }else{
                Toast.makeText(applicationContext, "Недостаточно баллов, текущее количество:$checkScore", Toast.LENGTH_SHORT).show()
            }
        }

        iv_helpCallBot.setOnLongClickListener {
            Toast.makeText(applicationContext, "50/50 правильный ответ от бота", Toast.LENGTH_SHORT).show()
            true
        }

        btn_wantAnswer.setOnClickListener {
            heClick = true
        }

        fun resetQuestion() {
            heClick = false
            countCharacter = 0
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
                } else {
                    this.cancel()
                    correctAnswer = dialogInit()
                    if (correctAnswer) {
                        Toast.makeText(
                            this@GameInterfaceActivity,
                            "+$rvPrice очков!",
                            Toast.LENGTH_SHORT
                        ).show()
                        score += rvPrice
                        prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                        tv_count.text = "Счет:$score"
                        cancel()
                    } else {
                        Toast.makeText(
                            this@GameInterfaceActivity,
                            "-$rvPrice очков!",
                            Toast.LENGTH_SHORT
                        ).show()
                        score -= rvPrice
                        prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                        tv_count.text = "Счет:$score"
                        cancel()
                    }
                }
                resetQuestion()
                cancel()
                countRound++
                tv_numberOfRound.text = "Раунд:$countRound"
                makeInvisibleAnswerPart()
                //TODO(надо будет добавить что-то для вывода результатов когда вопросы заканчиваются
                // + определять победу и набранные очки в зависимости single/multiplayer и мб сложности(для сингл))
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
            /*if (et_enterAnswer.text.isEmpty()) {
                Toast.makeText(this, "Вы не ввели ответ!\n-$rvPrice очков!", Toast.LENGTH_SHORT)
                    .show()
                score -= rvPrice
                prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                //в будущем можно будет апгрейдить и не давать отвечать пока не введет ответ или что нибудь еще помимо тоста
            }*/
            tv_count.text = "Счет:$score"
            time2.onFinish()
            progressBar.visibility = View.INVISIBLE
            tv_timer2.visibility = View.INVISIBLE
            tv_people.visibility = View.VISIBLE
            //TODO(в конце игры, когда будет выявлен победитель, нужно в SP +1 игроку добавить, be like:)
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
                iv_gi_back_to_menu.visibility = View.INVISIBLE
            }

            override fun onFinish() {
                if (!heClick or (progressBar.progress == 0)) {
                    tv_timer.text = "Время вышло!"
                    if (isSingle) {
                        score -= rvPrice
                        prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                        tv_count.text = "Счет:$score"
                        Toast.makeText(
                            this@GameInterfaceActivity,
                            "Вы решили не отвечать!\n-$rvPrice очков!",
                            Toast.LENGTH_SHORT
                        ).show()
                        resetQuestion()
                        cancel()
                        countRound++
                        tv_numberOfRound.text = "Раунд:$countRound"
                        btn_wantAnswer.visibility = View.INVISIBLE
                        makeInvisibleAnswerPart()
                        tv_people.visibility = View.VISIBLE
                    }
                    if (!isSingle) {
                        //TODO(WARNING! СПЕЦИАЛЬНО ДЛЯ ТЕМУРА, СПОНСОРА МОИХ РАННИХ СЕДИН)
                        // тк для него я уже одну проверку сделала вот еще одна:
                        //TODO(здесь нужна проверка, ответил ли вообще кто-то из игроков,
                        // если нет, переход к табл(visibility и вот это все), если да, идем дальше, но без возможности ввести ответ
                        // + в табл с игроками отображать чей ход. кароче когда сеть будет я это сама сделаю,
                        // а то там темурывсякие поломают все
                    }
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

        iv_gi_back_to_menu.setOnClickListener {
            finish()
            onBackPressed()
        }

        //TODO(когда игра закончится, нужно будет в  SP сохранить итоговый счет игрока за игру и в профиль, be like:)
//        wholeScore+=score (score предварительно умножить на коэф)
//        prefs.edit().putInt(APP_PREFERENCES_WHOLE_SCORE, score).apply()

        //TODO(а еще я не знаю где лежат эти тосты, которые показывают категорию,цену,ответ вопроса
        // так вот предлагаю их окончательно не убирать, а сделать красивый тост только с выводом категории и цены)
    }



    fun dialogInit():Boolean{
        return getDialogValueBack(this@GameInterfaceActivity)
    }



    private fun makeVisibleAnswerPart() {
        tv_count.visibility = View.VISIBLE
        tv_numberOfRound.visibility = View.VISIBLE
        btn_wantAnswer.visibility = View.INVISIBLE
        tv_timer.visibility = View.INVISIBLE
        et_enterAnswer.visibility = View.VISIBLE
        btn_finallAnswer.visibility = View.VISIBLE
        tv_timer2.visibility = View.VISIBLE
        tv_people.visibility = View.VISIBLE
        iv_helpCallBot.visibility = View.VISIBLE
        iv_getOneChar.visibility = View.VISIBLE
    }

    private fun makeInvisibleAnswerPart() {
        tv_count.visibility = View.VISIBLE
        tv_numberOfRound.visibility = View.VISIBLE
        et_enterAnswer.visibility = View.INVISIBLE
        btn_finallAnswer.visibility = View.INVISIBLE
        tv_timer2.visibility = View.INVISIBLE
        tv_textquestion.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        rv_questions.visibility = View.VISIBLE
        iv_gi_back_to_menu.visibility = View.VISIBLE
        tv_timer.visibility = View.INVISIBLE
        tv_people.visibility = View.VISIBLE
        iv_helpCallBot.visibility = View.INVISIBLE
        iv_getOneChar.visibility = View.INVISIBLE
    }

    private fun getPack(
        pack: String,
        isPackUri: Boolean,
        randomize: Boolean = true
    ): List<Category> {
        val dataStream: InputStream = if (isPackUri) {
            contentResolver.openInputStream(Uri.parse(pack))
                ?: throw FileNotFoundException()
        } else {
            assets.open(pack)
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
        startActivity(Intent(this, MainMenuActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        super.onBackPressed()
    }

    private var resultValue = false
    private fun getDialogValueBack(context: Context?): Boolean {
        val handler: Handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(mesg: Message?) {
                throw RuntimeException()
            }
        }
        val alert = AlertDialog.Builder(context,R.style.AlertDialogStyle)
        alert.setTitle("Правильный ответ")
        alert.setMessage("$rvAnswer")
        alert.setPositiveButton("Правильно") { dialog, id ->
            resultValue = true
            handler.sendMessage(handler.obtainMessage())
        }
        alert.setNegativeButton("Неправильно") { dialog, id ->
            resultValue = false
            handler.sendMessage(handler.obtainMessage())
        }
        alert.show()
        try {
            Looper.loop()
        } catch (e: RuntimeException) {
        }
        return resultValue
    }

}
