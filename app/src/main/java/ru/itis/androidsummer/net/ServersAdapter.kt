package ru.itis.androidsummer.net

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_server.*
import ru.itis.androidsummer.R
import ru.itis.androidsummer.data.Player
import ru.itis.androidsummer.data.Server

class ServersAdapter() :
    ListAdapter<Server, ServersViewHolder>(ServersDiffCallback()) {
    private var itemClickListener: ((Server) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ServersViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_server, parent, false)
    )

    override fun getItemId(position: Int) = getItem(position).id

    override fun onBindViewHolder(holder: ServersViewHolder, position: Int) {
        holder.bind(getItem(position), this::onItemClick)
    }

    private fun onItemClick(server: Server) {
        itemClickListener?.let { it(server) }
    }

    fun setOnItemClickListener(listener: (server: Server) -> Unit) {
        itemClickListener = listener
    }

    fun removeOnItemClickListener() {
        itemClickListener = null
    }
}

class ServersViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    @SuppressLint("SetTextI18n")
    fun bind(server: Server, listener: (Server) -> Unit) {
                    tv_serverInfo.setOnClickListener {
                            listener(server)
                    }
        tv_serverInfo.text = "${server.name} (${server.address})"
    }

}

class ServersDiffCallback : DiffUtil.ItemCallback<Server>() {
    override fun areItemsTheSame(oldItem: Server, newItem: Server): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Server, newItem: Server): Boolean = oldItem == newItem
}
