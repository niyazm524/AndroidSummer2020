package ru.itis.androidsummer

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.test.*
import ru.itis.androidsummer.data.Question
import ru.itis.androidsummer.parsers.ContentsXmlParser
import java.io.ByteArrayInputStream
import java.lang.Exception

class Test : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.test)
        try{
            Glide.with(this).load(BitmapFactory.decodeStream(ByteArrayInputStream(
                ContentsXmlParser.hashThing.get(Question(300,"","Rhino",false))))).into(imageView)
            /*
        imageView.setImageBitmap(BitmapFactory.decodeStream(
            ContentsXmlParser.hashThing.get(Question(300," ","Rhino",false))))*/
        } catch (e: Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }

    }
}