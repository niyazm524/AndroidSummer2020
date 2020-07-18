package ru.itis.androidsummer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.ScrollingMovementMethod
import android.view.View
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
import ru.itis.androidsummer.parsers.ContentsXmlParser.Companion.getQuestionsResource
import ru.itis.androidsummer.parsers.ContentsXmlParser.Companion.questionResources
import ru.itis.androidsummer.parsers.FileTypes
import ru.itis.androidsummer.parsers.FileTypes.Companion.checkFileType
import ru.itis.androidsummer.parsers.SiqParser
import ru.itis.androidsummer.parsers.SiqParser.Companion.resourceStorage
import ru.itis.androidsummer.simpleBot.SingleplayerBot
import ru.itis.androidsummer.utils.ProjectUtils.Companion.pickRandomQuestions
import java.io.*


class GameInterfaceActivity : AppCompatActivity() {
    private var media: MediaPlayer? = null
    private val questionsAdapter = QuestionsAdapter()
    private val siqParser = SiqParser()
    private lateinit var contentsXmlParser: ContentsXmlParser

    var rvAnswer: String? = null
    var countRound = 0
    var rvQuestion: String? = null
    var rvPrice: Int = 0
    var botHelpAnswer:Boolean = false
    var correctAnswer: Boolean = false
    var countCharacter = 0

    var heClick = false
    var heFinalClick = false

    lateinit var bot:SingleplayerBot
    var botScore = 0
    var botIsAnswer = false
    var botCorrectAnswer = false
    private lateinit var temporaryMusicFile: File

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)
        bot = SingleplayerBot("Bot",intent.getIntExtra("level",0))
        iv_image.visibility = View.GONE

        temporaryMusicFile = File(applicationContext.filesDir, "temp.mp3")

        val isSingle = intent.getBooleanExtra("isSingle", false)
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

        countRound = 1
        var score = prefs.getInt(APP_PREFERENCES_SCORE, 0)
        var victory = prefs.getInt(APP_PREFERENCES_VICTORY, 0)
        var wholeScore = prefs.getInt(APP_PREFERENCES_WHOLE_SCORE, 0)
        val helpSymbolPrice = 500
        val helpBotPrice = 800
        val me = prefs.getString(
            APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        ) + "(ты)"
        tv_count.text = "Счет:$score"
        tv_people.text = "ИГРОКИ: \n$me: $score\n${bot.name}: $botScore"
        tv_numberOfRound.text = "Раунд:$countRound"
        if (!isSingle) {
            tv_people.visibility = View.VISIBLE
            Toast.makeText(this, "Вы выбрали игру с друзьями!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Вы выбрали одиночную игру! Уровень: ${bot.getDifficult()}", Toast.LENGTH_SHORT).show()
        }
        //тут конечно теперь пустовато на экране с таблицей для одиночки, надо будет подумать над этим
        //TODO(сюда в тост вставь сложность после "уровень:")

        iv_getOneChar.setOnClickListener {

            val checkScore = prefs.getInt(APP_PREFERENCES_WHOLE_SCORE, 0)
            if (checkScore >= helpSymbolPrice) {

                countCharacter++
                if (countCharacter <= (rvAnswer?.lastIndex ?: 0) + 1) {
                    prefs.edit().putInt(APP_PREFERENCES_WHOLE_SCORE, checkScore - helpSymbolPrice)
                        .apply()
                    et_enterAnswer.setText(rvAnswer?.subSequence(0, countCharacter))
                } else {
                    Toast.makeText(this, "Все символы открыты", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Недостаточно баллов, текущее количество:$checkScore",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        iv_getOneChar.setOnLongClickListener {
            Toast.makeText(this, "Открыть один символ ответа: $helpSymbolPrice", Toast.LENGTH_SHORT).show()
            true
        }



        iv_helpCallBot.setOnLongClickListener {
            Toast.makeText(this, "Звонок боту: $helpBotPrice", Toast.LENGTH_SHORT)
                .show()
            true
        }

        btn_wantAnswer.setOnClickListener {
            heClick = true
            stopMusicIfPlaying()
        }


        fun botHelpAnswer():Boolean{
            return (Math.random()*100+1).toInt() >= 50
        }

        fun canAnswer(bool:Boolean){
            btn_finallAnswer.isClickable = bool
            btn_wantAnswer.isClickable = bool
            et_enterAnswer.isEnabled = bool
            iv_getOneChar.isEnabled = bool
            iv_helpCallBot.isEnabled = bool
        }

        val time2 = object : CountDownTimer(20000, 1000) {
            override fun onFinish() {
                    if (botIsAnswer) {
                        if (botCorrectAnswer) {
                            Toast.makeText(
                                this@GameInterfaceActivity,
                                "${bot.name} ошибся \n -$rvPrice",
                                Toast.LENGTH_SHORT
                            ).show()
                            botScore -= rvPrice
                        } else {
                            Toast.makeText(
                                this@GameInterfaceActivity,
                                "${bot.name} ответил правильно \n +$rvPrice",
                                Toast.LENGTH_SHORT
                            ).show()
                            botScore += rvPrice
                        }
                        resetQuestion()
                        botIsAnswer = false
                        bot.countdown = 3
                        canAnswer(true)
                        botScore += rvPrice
                        countRound++
                        tv_numberOfRound.text = "Раунд:$countRound"

                        makeInvisibleAnswerPart()
                    } else {
                        if (!botHelpAnswer) {
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
                                resetQuestion()
                                countRound++
                                tv_numberOfRound.text = "Раунд:$countRound"
                                makeInvisibleAnswerPart()
                            } else if(et_enterAnswer.text.isNotEmpty()) {
                                this.cancel()
                                openCheckingAnswerDialog { isCorrect ->
                                    correctAnswer = isCorrect
                                    checkAnswer()
                                }
                            }
                            //TODO(надо будет добавить что-то для вывода результатов когда вопросы заканчиваются
                            // + определять победу и набранные очки в зависимости single/multiplayer и мб сложности(для сингл))
                        } else {
                            correctAnswer = botHelpAnswer()
                            checkAnswer()
                        }

                    }
                tv_people.text = "ИГРОКИ: \n$me: $score\n${bot.name}: $botScore"
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tv_timer2.text = "Осталось времени:" + millisUntilFinished / 1000
                progressBar.progress = (millisUntilFinished / 1000).toInt()
                if(botIsAnswer){
                    if(bot.countdown > 0){
                        bot.countdown--
                    } else {
                        botCorrectAnswer = bot.botCorrectAnswer()
                        bot.countdown = 3
                        cancel()
                        onFinish()
                    }
                }else {
                    if (heFinalClick) {
                        progressBar.progress = progressBar.max
                        cancel()
                        onFinish()
                    }
                }
            }

        }

        iv_helpCallBot.setOnClickListener {

            val checkScore = prefs.getInt(APP_PREFERENCES_WHOLE_SCORE, 0)
            if (checkScore >= helpBotPrice) {
                prefs.edit().putInt(APP_PREFERENCES_WHOLE_SCORE, checkScore - helpBotPrice).apply()

                botHelpAnswer = true
                time2.onFinish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Недостаточно баллов, текущее количество:$checkScore",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun finalAnswerBtnInit(){
            tv_count.text = "Счет:$score"
            time2.onFinish()
            progressBar.visibility = View.INVISIBLE
            tv_timer2.visibility = View.INVISIBLE
            tv_people.visibility = View.VISIBLE
        }

        btn_finallAnswer.setOnClickListener {
            if (iv_image.isShown) {
                iv_image.setImageDrawable(null)
                iv_image.visibility = View.GONE
            }
            if (et_enterAnswer.text.isEmpty()) {
                Toast.makeText(this, "Вы не ввели ответ!\n-$rvPrice очков!", Toast.LENGTH_SHORT)
                    .show()
                score -= rvPrice
                prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
                time2.cancel()
                time2.onFinish()
                resetQuestion()
                tv_count.text = "Счет:$score"
                tv_people.text = "ИГРОКИ: \n$me: $score\n${bot.name}: $botScore"
                makeInvisibleAnswerPart()
                //в будущем можно будет апгрейдить и не давать отвечать пока не введет ответ или что нибудь еще помимо тоста
            } else{
                finalAnswerBtnInit()
            }

            //TODO(в конце игры, когда будет выявлен победитель, нужно в SP +1 игроку добавить, be like:)
//            victory++
//            prefs.edit().putInt(APP_PREFERENCES_VICTORY, victory).apply()
        }


        val time = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_timer.text = "Осталось времени:" + millisUntilFinished / 1000
                progressBar.progress = progressBar.max
                if(bot.countdown > 0){
                    bot.countdown--
                } else{
                    botIsAnswer = bot.botAnswer()
                }
                if (heClick || botIsAnswer) {
                    progressBar.progress = progressBar.max
                    bot.countdown = 3
                    if(botIsAnswer){
                        canAnswer(false)
                        Toast.makeText(this@GameInterfaceActivity,"Бот решил ответить",Toast.LENGTH_SHORT).show()
                    }
                    makeVisibleAnswerPart()
                    onFinish()
                    time2.start()
                    cancel()
                }
                progressBar.progress = (millisUntilFinished / 1000).toInt()
                iv_gi_back_to_menu.visibility = View.INVISIBLE
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

            override fun onFinish() {
                if (!botIsAnswer) {
                    if (!heClick or (progressBar.progress == 0)) {
                        tv_timer.text = "Время вышло!"
                        if (isSingle) {
                            tv_count.text = "Счет:$score"
                            Toast.makeText(
                                this@GameInterfaceActivity,
                                "Никто не ответил",
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
        }
        questionsAdapter.setOnItemClickListener { question ->
            btn_wantAnswer.visibility = View.VISIBLE
            rv_questions.visibility = View.INVISIBLE
            tv_textquestion.visibility = View.VISIBLE
            rvAnswer = question.answer
            rvQuestion = question.question
            rvPrice = question.price
            tv_textquestion.text = rvQuestion
            if (checkFileType(questionResources[question]) == FileTypes.IMAGE_FILE){
                tv_textquestion.text = ""
                if (getQuestionsResource(question)!=null) {
                    iv_image.visibility = View.VISIBLE
                    ByteArrayInputStream(getQuestionsResource(question)).buffered().use { it ->
                        iv_image.setImageBitmap(
                            BitmapFactory.decodeStream(it)
                        )
                    }
                    iv_image.setOnClickListener {
                        Toast.makeText(this, question.question, Toast.LENGTH_LONG).show()
                    }
                } else
                    Toast.makeText(this,"отсутствует ресурсный файл изображения",Toast.LENGTH_LONG).show()
            }
            if (checkFileType(questionResources[question]) == FileTypes.MUSIC_FILE){

                try {
                    temporaryMusicFile.delete()
                    temporaryMusicFile.writeBytes(getQuestionsResource(question)
                        ?:throw FileNotFoundException("отсутствует ресурсный файл аудио"))
                    media = MediaPlayer()
                    FileInputStream(temporaryMusicFile).use { it ->
                        media!!.setDataSource(it.fd)
                    }
                    media!!.prepare()
                    media!!.start()
                    Toast.makeText(this,R.string.game_text_music_notification,Toast.LENGTH_SHORT)
                        .show()
                }catch(e: Exception){
                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
                }
            }
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

    private fun stopMusicIfPlaying() {
        media?.apply {
            if (isPlaying) {
                stop()
                release()
                media = null
            }
        }
        temporaryMusicFile.delete()
    }

    @SuppressLint("SetTextI18n")
    private fun resetQuestion() {
        tv_people.text =
            "ИГРОКИ: \n${getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).getString(
                APP_PREFERENCES_REGISTRATION,
                resources.getString(R.string.profile_text_default_name)
            ) + "(ты)"}\n${bot.name}:$botScore"
        stopMusicIfPlaying()
        heClick = false
        countCharacter = 0
        heFinalClick = false
        rvAnswer = null
        rvQuestion = null
        rvPrice = 0
        et_enterAnswer.setText("")
        botHelpAnswer = false
    }

    @SuppressLint("SetTextI18n")
    fun checkAnswer(){
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        var score = prefs.getInt(APP_PREFERENCES_SCORE, 0)
        if (correctAnswer) {
            Toast.makeText(
                this@GameInterfaceActivity,
                "+$rvPrice очков!",
                Toast.LENGTH_SHORT
            ).show()
            score += rvPrice
            prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
            tv_count.text = "Счет:$score"
        } else {
            Toast.makeText(
                this@GameInterfaceActivity,
                "-$rvPrice очков!",
                Toast.LENGTH_SHORT
            ).show()
            score -= rvPrice
            prefs.edit().putInt(APP_PREFERENCES_SCORE, score).apply()
            tv_count.text = "Счет:$score"
        }
        resetQuestion()
        countRound++
        tv_numberOfRound.text = "Раунд:$countRound"
        makeInvisibleAnswerPart()
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
        iv_image.visibility = View.GONE
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
            category.questions.size > 0
        }


    override fun onBackPressed() {
        val prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putInt(APP_PREFERENCES_SCORE, 0).apply()
        questionResources.clear()
        resourceStorage.clear()
        if (media!= null &&  media!!.isPlaying) {
            media!!.stop()
            media!!.release()
            media = null
        }
        startActivity(Intent(this, MainMenuActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        super.onBackPressed()
    }


    private fun openCheckingAnswerDialog(answerCallback: (isCorrect: Boolean) -> Unit) {
        val alert = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        alert.setTitle("Правильный ответ")
        alert.setMessage("$rvAnswer")
        alert.setPositiveButton("Правильно") { _, _ ->
            answerCallback(true)

        }
        alert.setNegativeButton("Неправильно") { _, _ ->
            answerCallback(false)
        }
        alert.show()
    }

}
