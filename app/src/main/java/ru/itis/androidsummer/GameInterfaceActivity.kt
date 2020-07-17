package ru.itis.androidsummer

import android.annotation.SuppressLint
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
import ru.itis.androidsummer.parsers.SiqParser
import ru.itis.androidsummer.parsers.SiqParser.Companion.resourceStorage
import ru.itis.androidsummer.parsers.FileTypes
import ru.itis.androidsummer.parsers.FileTypes.Companion.checkFileType
import ru.itis.androidsummer.utils.ProjectUtils.Companion.pickRandomQuestions
import java.io.*


class GameInterfaceActivity : AppCompatActivity() {
    private var media: MediaPlayer? = null
    private val questionsAdapter = QuestionsAdapter()
    private val siqParser = SiqParser()
    private lateinit var contentsXmlParser: ContentsXmlParser

    var rvAnswer: String? = null
    var rvQuestion: String? = null
    var rvPrice: Int = 0

    var heClick = false
    var heFinalClick = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_interface)
        iv_image.visibility = View.GONE

        val temporaryMusicFile = File(applicationContext.filesDir, "temp.mp3")

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
        val me = prefs.getString(
            APP_PREFERENCES_REGISTRATION,
            resources.getString(R.string.profile_text_default_name)
        ) + "(ты)"
        tv_count.text = "Счет:$score"
        tv_people.text = "ИГРОКИ: \n$me"
        tv_numberOfRound.text = "Раунд:$countRound"
        /*
        if (!isSingle) {
            Toast.makeText(this, "Вы выбрали игру с друзьями!", Toast.LENGTH_SHORT).show()
        }
        if (isSingle) Toast.makeText(this, "Вы выбрали одиночную игру! Уровень:", Toast.LENGTH_SHORT).show()*/
        //тут конечно теперь пустовато на экране с таблицей для одиночки, надо будет подумать над этим
        //TODO(сюда в тост вставь сложность после "уровень:")



        btn_wantAnswer.setOnClickListener {
            if (media != null) {
                if (media!!.isPlaying) {
                    media!!.stop()
                    media!!.release()
                    media = null
                }
            }
            heClick = true
            temporaryMusicFile.delete()
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
            if (iv_image.isShown) {
                iv_image.setImageDrawable(null)
                iv_image.visibility = View.GONE
            }
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
                    if(!isSingle){
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
            //Toast.makeText(this, "$question", Toast.LENGTH_SHORT).show()
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
        }

        //TODO(когда игра закончится, нужно будет в  SP сохранить итоговый счет игрока за игру и в профиль, be like:)
//        wholeScore+=score (score предварительно умножить на коэф)
//        prefs.edit().putInt(APP_PREFERENCES_WHOLE_SCORE, score).apply()

        //TODO(а еще я не знаю где лежат эти тосты, которые показывают категорию,цену,ответ вопроса
        // так вот предлагаю их окончательно не убирать, а сделать красивый тост только с выводом категории и цены)
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

}
