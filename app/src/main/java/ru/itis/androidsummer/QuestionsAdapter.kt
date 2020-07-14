package ru.itis.androidsummer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_cell.*
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question


class QuestionsAdapter : RecyclerView.Adapter<CategoriesViewHolder>() {
    private val categories = ArrayList<Category>()
    private var itemClickListener: ((Question) -> Unit)? = null


    fun inputCategory(category: Category) {
        categories.add(category)
    }

    fun inputList(category: List<Category>) {
        category.forEach {
            inputCategory(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cell, parent, false)
        )
    }

    override fun getItemCount(): Int {
        var count = 0
        for (category in categories) {
            count += 1 + category.questions.size
        }
        return count
    }

    fun getCategorySize(): Int {
        return categories[0].questions.size
    }

    fun getCategoryCount(): Int {
        return categories.size
    }


    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        var index = 0
        val categoryCount = getCategoryCount()
        var category = position

        while (category >= categoryCount) {
            category -= categoryCount
            index++
        }

        holder.bind(
            categories[category], (index - 1).let { if (it < 0) null else it }, this::onItemClick
        )
    }

    private fun onItemClick(question: Question) {
        itemClickListener?.let { it(question) }

    }

    fun setOnItemClickListener(listener: (question: Question) -> Unit) {
        itemClickListener = listener
    }

    fun removeOnItemClickListener() {
        itemClickListener = null
    }

}

class CategoriesViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(category: Category, index: Int?, listener: (Question) -> Unit) {
        tv_help2.setOnClickListener {
            if (index != null) {
                category.questions.getOrNull(index)?.let { question ->
                    listener(question)
                }
                tv_help2.visibility = View.INVISIBLE
            }
        }

        tv_help2.textSize = 26F
        if (index != null) {
            val question = category.questions.getOrNull(index)
            if (question != null) {
                tv_help2.text = question.price.toString()
                tv_help2.isClickable = true
                tv_help2.setTextColor(Color.WHITE)
            } else {
                tv_help2.text = ""
                tv_help2.isClickable = false
                tv_help2.background = null
            }
        } else {
            tv_help2.text = category.title
            tv_help2.background = null
            tv_help2.textSize = 16F
        }
    }

}


