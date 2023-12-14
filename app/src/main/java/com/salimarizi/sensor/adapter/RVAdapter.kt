package com.salimarizi.sensor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RVAdapter<T>(
    private val items: List<T>,
    private val itemViewHolderId: Int,
    private val onCreateItem: ((View, T) -> Unit),
    private val onItemClick: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<RVAdapter.RVHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(itemViewHolderId, parent, false)
        return RVHolder(view, onCreateItem, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RVHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    class RVHolder<T>(
        itemView: View,
        private val onCreateItem: ((View, T) -> Unit),
        private val onItemClick: ((Int) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: T) {
            onCreateItem.invoke(itemView, item)
            onItemClick?.let { action ->
                itemView.setOnClickListener {
                    action.invoke(adapterPosition)
                }
            }
        }
    }
}