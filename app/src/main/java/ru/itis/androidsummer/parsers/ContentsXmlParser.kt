package ru.itis.androidsummer.parsers

import org.xmlpull.v1.XmlPullParser
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question
import ru.itis.androidsummer.parsers.SiqParser.Companion.resourceStorage
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class ContentsXmlParser(private val parser: XmlPullParser) {
    companion object {
        val questionResources: HashMap<Question, String> = HashMap()
        val resourceTypes: HashMap<Question, String> = HashMap()
        fun getQuestionsResource(question: Question): ByteArray? {
            return resourceStorage[questionResources[question]]
        }
    }
    fun parseQuestion(contentsStream: InputStream): List<Category> {
        val streamReader = InputStreamReader(contentsStream, Charset.forName("UTF-8"))
        while (streamReader.read()!='>'.toInt()){
            continue
        }
        parser.setInput(streamReader)
        val categories = ArrayList<Category>()
        while (parser.eventType!=XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "theme") {
                val category = parser.getAttributeValue(0)
                categories.add(Category(category, ArrayList<Question>()))
                parser.next()
            } else if (parser.eventType == XmlPullParser.START_TAG && parser.name == "question") {
                var price = 0
                var question = ""
                var resource = ""
                var right = ""
                //костыль: пока только текстовые вопросы
                var isTextQuestion = true
                loop@ while (!(parser.eventType == XmlPullParser.END_TAG && parser.name == "question")) {
                    if (parser.eventType == XmlPullParser.START_TAG) {
                        when (parser.name) {
                            "question" -> {
                                price = parser.getAttributeValue(0).toInt()
                                parser.next()
                            }
                            "atom" -> {
                                if (parser.attributeCount >= 1){
                                    isTextQuestion = false
                                    parser.next()
                                    resource = parser.text
                                    continue@loop
                                }
                                parser.next()
                                question = parser.text
                            }
                            "answer" -> {
                                parser.next()
                                right = parser.text
                            }
                        }
                    }
                    parser.next()
                }
                if (isTextQuestion)
                    categories.last().transformIntoArray().add(Question(price, question, right))
                else{
                    val question = Question(price, question, right)
                    categories.last().transformIntoArray().add(question)
                    questionResources[question] = resource.replace("@","")
                    resourceTypes[question] = resource.replaceBefore(".","")
                }
            } else
                parser.next()
        }
        return categories
    }
}
