package ru.itis.androidsummer.utils

enum class FileTypes {
    MUSIC_FILE,IMAGE_FILE,OTHER_FILE;
    companion object {
        fun checkFileType(suffix: String?): FileTypes {
            if (suffix == ".mp3")
                return MUSIC_FILE
            if (suffix == ".jpg" || suffix == ".jpeg") {
                return IMAGE_FILE
            }
            return OTHER_FILE
        }
    }
}