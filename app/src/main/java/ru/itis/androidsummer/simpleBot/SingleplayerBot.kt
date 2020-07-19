package ru.itis.androidsummer.simpleBot

class SingleplayerBot(val name: String, private val difficult: Int) {
    var countdown = 5
    fun getDifficult(): String {
        return when (difficult) {
            0 -> "Легко"
            1 -> "Средне"
            else -> "Тяжело"
        }
    }

    private fun getDifficultCoefficient() : Double{
        return when (difficult) {
            0 -> 0.2
            1 -> 0.35
            else -> 0.5

        }
    }

    fun botAnswer():Boolean{
        val randomBot = ((Math.random()*100+1)*getDifficultCoefficient()).toInt()
        val random = (Math.random()*100+1).toInt()
        return randomBot >= random
    }

    fun botCorrectAnswer():Boolean{
        val randomBot = ((Math.random()*60+41)*(getDifficultCoefficient()+0.5)).toInt()
        val random = (Math.random()*100+1).toInt()
        return randomBot >= random
    }


}

