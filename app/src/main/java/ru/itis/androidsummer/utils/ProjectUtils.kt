package ru.itis.androidsummer.utils

import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question

class ProjectUtils {
    companion object {
        fun pickRandomQuestions(
            categoryList: List<Category>,
            maxQuestions: Int,
            maxCategories: Int
        ): List<Category> {
            val newCategoryList = ArrayList<Category>()
            var maxIndex = maxCategories
            val arrayOfCategoriesNames = ArrayList<String>(maxCategories)
            if (categoryList.size < maxCategories)
                maxIndex = categoryList.size
            for (index in 1..maxIndex) {
                val newQuestionArray = ArrayList<Question>()
                var category = categoryList.random()
                while (category.transformIntoArray().size == 0
                    || arrayOfCategoriesNames.contains(category.title)
                ) {
                    category = categoryList.random()
                }
                arrayOfCategoriesNames.add(category.title)
                val questionArray = category.transformIntoArray()
                var maxIndex2 = maxQuestions
                if (questionArray.size < maxQuestions) {
                    maxIndex2 = questionArray.size
                }
                for (index2 in 1..maxIndex2) {
                    newQuestionArray.add(questionArray.random())
                }
                newCategoryList.add(
                    Category(category.title,
                        newQuestionArray.sortedBy { question -> question.price })
                )
            }
            return newCategoryList
        }
    }
}