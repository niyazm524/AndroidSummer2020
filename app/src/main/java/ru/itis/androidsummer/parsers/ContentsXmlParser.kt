package ru.itis.androidsummer.parsers

import org.xmlpull.v1.XmlPullParser
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question
import java.io.InputStream
import java.io.InputStreamReader

class ContentsXmlParser(private val parser: XmlPullParser) {
    fun parseQuestion(contentsStream: InputStream): List<Category> {
        val streamReader = InputStreamReader(contentsStream)
        parser.setInput(streamReader)
        val categories = ArrayList<Category>()
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "theme") {
                val category = parser.getAttributeValue(0)
                categories.add(Category(category, ArrayList<Question>()))
                parser.next()
            } else if (parser.eventType == XmlPullParser.START_TAG && parser.name == "question") {
                var price = 0
                var question = ""
                var right = ""
                //костыль: пока только текстовые вопросы
                var isTextQuestion = true
                while (!(parser.eventType == XmlPullParser.END_TAG && parser.name == "question")) {
                    if (parser.eventType == XmlPullParser.START_TAG) {
                        when (parser.name) {
                            "question" -> {
                                price = parser.getAttributeValue(0).toInt()
                                parser.next()
                            }
                            "atom" -> {
                                if (parser.attributeCount >= 1){
                                    isTextQuestion = false
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
            } else
                parser.next()
        }
        return categories
    }
}
