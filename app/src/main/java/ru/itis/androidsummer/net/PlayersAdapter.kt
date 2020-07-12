package ru.itis.androidsummer.net

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_player.*
import ru.itis.androidsummer.R
import ru.itis.androidsummer.data.Player

class PlayersAdapter :
    ListAdapter<Player, PlayersViewHolder>(PlayersDiffCallback()) {
    private var itemClickListener: ((Player) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlayersViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
    )

    override fun getItemId(position: Int) = getItem(position).id

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        holder.bind(getItem(position), this::onItemClick)
    }

    private fun onItemClick(player: Player) {
        itemClickListener?.let { it(player) }
    }

    fun setOnItemClickListener(listener: (player: Player) -> Unit) {
        itemClickListener = listener
    }

    fun removeOnItemClickListener() {
        itemClickListener = null
    }
}

class PlayersViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    @SuppressLint("SetTextI18n")
    fun bind(player: Player, listener: (Player) -> Unit) {
        tv_playerInfo.setOnClickListener {
            listener(player)
        }

        tv_playerInfo.text = "(${player.id}) ${player.name} {${player.rating}}"

    }

}

class PlayersDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean = oldItem == newItem
}
