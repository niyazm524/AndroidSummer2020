package ru.itis.androidsummer.data

data class Category(val title: String, val questions: List<Question>){

    fun transformIntoArray(): ArrayList<Question>{
        return questions as ArrayList<Question>
    }

}



