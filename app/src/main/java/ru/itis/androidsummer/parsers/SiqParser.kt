package ru.itis.androidsummer.parsers

import android.net.Uri
import ru.itis.androidsummer.parsers.FileTypes.Companion.checkFileType
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
                }else if (checkFileType(zip.name) == FileTypes.MUSIC_FILE ||
                    checkFileType(zip.name) == FileTypes.IMAGE_FILE)
                    resourceStorage[Uri.decode(zip.name.replaceBefore('/',"")
                        .replace("/",""))] = stream.readBytes()
                stream.closeEntry()
                zip = stream.nextEntry
            }
        }
        return contentsBytes
    }
}
