package com.justappz.aniyomitv.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseRecyclerViewAdapter<T, VB : ViewBinding>(
    private var items: List<T> = listOf(),
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val bind: VB.(T, Int) -> Unit
) : RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder<VB>>() {

    var onItemClick: ((T, Int) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    /**
     * This method updates the entire list and notifies the adapter
     * Useful when you want to replace the current list with a new one
     * @param newItems List of new items to be set
     * */
    fun updateList(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * This method adds new items to the existing list and notifies the adapter
     * Useful for infinite scrolling or pagination scenarios
     * @param newItems List of new items to be added
     * */
    fun addToList(newItems: List<T>) {
        val oldSize = items.size
        items = items + newItems
        notifyItemRangeInserted(oldSize, newItems.size)
    }

    /**
     * This method retrieves an item at a specific position
     * @param position The index of the item to retrieve
     * @return The item at the specified position
     */
    fun getItem(position: Int): T = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        holder.binding.bind(items[position], position)
        holder.binding.root.setOnClickListener {
            onItemClick?.invoke(items[position], position)
        }
    }

    override fun getItemCount(): Int = items.size

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}
