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
     * This method updates the entire list and notifies the adapter
     * Useful when you want to replace the current list with a new one
     * @param newItems List of new items to be set
     * */
    fun updateList(newItems: List<T>, recyclerView: RecyclerView) {
        // Save current focus position
        val focusedView = recyclerView.findFocus()
        val focusedPos = if (focusedView != null) {
            recyclerView.getChildAdapterPosition(focusedView)
        } else RecyclerView.NO_POSITION

        // Update list + notify
        items = newItems
        notifyItemRangeChanged(0, newItems.size)

        // Restore focus
        if (focusedPos != RecyclerView.NO_POSITION && focusedPos < itemCount) {
            recyclerView.post {
                recyclerView.findViewHolderForAdapterPosition(focusedPos)?.itemView?.requestFocus()
            }
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
