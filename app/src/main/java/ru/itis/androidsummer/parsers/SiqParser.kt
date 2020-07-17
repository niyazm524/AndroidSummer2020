package ru.itis.androidsummer.parsers

import android.net.Uri
import java.io.InputStream
import java.util.zip.ZipInputStream

class SiqParser {
    companion object {
        val resourceStorage: HashMap<String, ByteArray> = HashMap()
    }
    fun parseSiq(file: InputStream): ByteArray? {
        var contentsBytes: ByteArray? = null
        val stream = ZipInputStream(file).use { stream ->
            var zip = stream.nextEntry
            while (zip != null) {
                if (zip.name == "content.xml") {
                    contentsBytes = stream.readBytes()
                }else if (zip.name.contains(".jpg") || zip.name.contains(".jpeg")
                    ||zip.name.contains(".mp3"))
                    resourceStorage[Uri.decode(zip.name.replaceBefore('/',"")
                        .replace("/",""))] = stream.readBytes()
                stream.closeEntry()
                zip = stream.nextEntry
            }
        }
        return contentsBytes
    }
}
