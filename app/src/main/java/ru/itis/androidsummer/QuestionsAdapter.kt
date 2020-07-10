package ru.itis.androidsummer

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_cell.*
import ru.itis.androidsummer.data.Category
import ru.itis.androidsummer.data.Question


class QuestionsAdapter : RecyclerView.Adapter<CategoriesViewHolder>() {
    private val categories = ArrayList<Category>()
    private var itemClickListener: ((Question) -> Unit)? = null


    fun inputCategory(category:Category){
        categories.add(category)
    }

    fun inputList(category:List<Category>){
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

    fun getCategorySize(): Int{
        return categories[0].questions.size
    }

    fun getCategoryCount(): Int{
        return categories.size
    }





    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        var index = position
        var i = 0
        while(i < categories.size){
            val size = categories[i].questions.size +1
            if(index >= size){
                index -= size
                i++
            } else{
                break
            }
        }
        holder.bind(
            categories[i], (index - 1).let { if(it < 0) null else it },this::onItemClick
        )
    }

    private fun onItemClick(question: Question) {
        itemClickListener?.let { it(question) }
    }

    fun setOnItemClickListener(listener: (question:Question) -> Unit) {
        itemClickListener = listener
    }

    fun removeOnItemClickListener() {
        itemClickListener = null
    }

}

class CategoriesViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    fun bind(category: Category, index: Int?,listener: (Question) -> Unit) {
        tv_text.setOnClickListener {
            if (index != null) {
                category.questions.getOrNull(index)?.let { it1 ->
                    listener(
                        it1
                    )
                }
            }
        }
        tv_text.textSize = 25F
        if (index != null) {
            tv_text.text = category.questions.getOrNull(index)?.price?.toString() ?: "n"
            tv_text.isClickable = true
            tv_text.setTextColor(Color.BLUE)

        } else {
            tv_text.text = category.title
            tv_text.background = null
            tv_text.textSize = 14F
        }
    }

}


