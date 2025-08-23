package com.justappz.aniyomitv.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BasePagingAdapter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val bind: VB.(T, Int) -> Unit,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, BasePagingAdapter.BaseViewHolder<VB>>(diffCallback) {

    var onItemClick: ((T, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val item = getItem(position) ?: return
        holder.binding.bind(item, position)
        holder.binding.root.setOnClickListener { onItemClick?.invoke(item, position) }
    }

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}
