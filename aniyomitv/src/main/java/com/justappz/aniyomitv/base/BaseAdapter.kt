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

    /**
     * Updates the entire list using DiffUtil for efficient changes
     * @param newItems List of new items to be set
     */
    fun updateList(newItems: List<T>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    private class DiffCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>
    ) : androidx.recyclerview.widget.DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
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
     * This method updates a single item at a specific position
     * Useful when you only want to refresh one element (e.g., selection state)
     * @param position The index of the item to update
     * @param newItem The new item to be set at the position
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateItemAt(position: Int, newItem: T) {
        if (position in items.indices) {
            items = items.toMutableList().apply {
                this[position] = newItem
            }
            notifyItemChanged(position)
        }
    }

    /**
     * This method retrieves an item at a specific position
     * @param position The index of the item to retrieve
     * @return The item at the specified position
     */
    fun getItem(position: Int): T = items[position]

    /**
     * This method retrieves the current list of items held by the adapter
     * @return The current list of items
     */
    fun getCurrentList(): List<T> = items

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
