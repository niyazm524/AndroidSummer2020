package ru.itis.androidsummer.parsers

enum class FileTypes {
    MUSIC_FILE,IMAGE_FILE,OTHER_FILE;
    companion object {
        fun checkFileType(resource: String?): FileTypes {
            if (resource != null) {
                if (resource.contains(".mp3"))
                    return MUSIC_FILE
                if (resource.contains(".jpg") || resource.contains(".jpeg")) {
                    return IMAGE_FILE
                }
            }
            return OTHER_FILE
        }
    }
}