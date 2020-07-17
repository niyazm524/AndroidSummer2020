package ru.itis.androidsummer.simpleBot

class SingleplayerBot(val name: String, private val difficult: Int) {
    var countdown = 3
    fun getDifficult(): String {
        return when (difficult) {
            0 -> "Easy"
            1 -> "Medium"
            else -> "Hard"
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
        val randomBot = ((Math.random()*60+41)*(getDifficultCoefficient()+0.4)).toInt()
        val random = (Math.random()*100+1).toInt()
        return randomBot >= random
    }


}

