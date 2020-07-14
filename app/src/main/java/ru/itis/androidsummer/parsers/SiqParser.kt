package ru.itis.androidsummer.parsers

import java.io.InputStream
import java.util.zip.ZipInputStream

class SiqParser {
    fun parseSiq(file: InputStream): ByteArray? {
        var contentsBytes: ByteArray? = null
        val stream = ZipInputStream(file).use { stream ->
            var zip = stream.nextEntry
            while (zip != null) {
                if (zip.name == "content.xml") {
                    contentsBytes = stream.readBytes()
                    break
                }
                stream.closeEntry()
                zip = stream.nextEntry
            }
        }

        return contentsBytes
    }
}
