package com.justappz.aniyomitv.base

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.Presenter

/**
 * A generic base adapter for Leanback that reduces boilerplate.
 */
open class BaseArrayObjectAdapter<T : Any>(
    presenter: Presenter
) : ArrayObjectAdapter(presenter) {

    /**
     * Replace all items in the adapter.
     */
    fun setItems(items: List<T>) {
        clear()
        addAll(0, items)
    }

    /**
     * Return the current list as a typed list.
     */
    @Suppress("UNCHECKED_CAST")
    fun getItems(): List<T> {
        return (0 until size())
            .mapNotNull { get(it) as? T }  // safe cast, ignores any nulls or wrong types
    }

    /**
     * Update a specific item and notify.
     */
    fun updateItem(position: Int, newItem: T) {
        if (position in 0 until size()) {
            replace(position, newItem)
        }
    }

    /**
     * Select a single item (assumes T has an isSelected field).
     * Deselects previously selected item automatically.
     */
    fun selectItem(position: Int, selector: (T) -> Boolean, markSelected: (T, Boolean) -> Unit) {
        val items = getItems().toMutableList()

        val oldSelectedIndex = items.indexOfFirst(selector)
        if (oldSelectedIndex != -1) {
            markSelected(items[oldSelectedIndex], false)
            updateItem(oldSelectedIndex, items[oldSelectedIndex])
        }

        if (position in items.indices) {
            markSelected(items[position], true)
            updateItem(position, items[position])
        }
    }
}
