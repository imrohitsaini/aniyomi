package com.justappz.aniyomitv.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import androidx.viewbinding.ViewBinding

/**
 * A generic BasePresenter that simplifies creating Leanback Presenters
 * with ViewBinding support.
 */
abstract class BasePresenter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root).apply {
            view.tag = binding // Store binding inside root view's tag for reuse
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val binding = viewHolder.view.tag as VB
        bind(binding, item as T)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Override if needed
    }

    protected abstract fun bind(binding: VB, item: T)
}
