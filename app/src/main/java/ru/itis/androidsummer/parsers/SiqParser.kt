package ru.itis.androidsummer.parsers

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream
import kotlin.collections.HashMap

class SiqParser {
    companion object {
        val hash: HashMap<String, ByteArray> = HashMap()
    }

    fun parseSiq(file: InputStream): ByteArray? {
        var contentsBytes: ByteArray? = null
        val stream = ZipInputStream(file).use { stream ->
            var zip = stream.nextEntry
            while (zip != null) {
                if (zip.name == "content.xml") {
                    contentsBytes = stream.readBytes()
                }else if (zip.name.contains(".jpg") || zip.name.contains(".mp3"))
                    hash.put(zip.name.replaceBefore('/',"").replace("/",""),
                        stream.readBytes())
                stream.closeEntry()
                zip = stream.nextEntry
            }
        }

        return contentsBytes
    }
}
