package com.justappz.aniyomitv.main.ui.adapters

import android.util.Log
import com.justappz.aniyomitv.base.BaseArrayObjectAdapter
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.ui.presenter.TabPresenter

class TabAdapter : BaseArrayObjectAdapter<MainScreenTab>(TabPresenter()) {

    private val tag = "TabAdapter"

    fun currentList(): List<MainScreenTab> = getItems()

    fun setTabs(tabs: List<MainScreenTab>) {
        setItems(tabs)
    }

    fun selectLastTab() {
        Log.i(tag, "selectTab()")
        val items = getItems().toMutableList()
        val position = items.lastIndex

        if (position != -1) {
            Log.i(tag, "updateItem()")
            items[position].isSelected = true
            Log.i(tag, "select last tab")
            updateItem(position, items[position])
        }
    }

    fun selectTab(position: Int) {
        Log.i(tag, "selectTab()")
        val items = getItems().toMutableList()

        // deselectLastTab
        deselectLastTab()

        // Select new
        Log.i(tag, "updateItem()")
        items[position].isSelected = true
        updateItem(position, items[position])
    }

    fun deselectLastTab() {
        Log.i(tag, "deselectTab()")
        val items = getItems().toMutableList()

        // Find currently selected tab
        val oldSelectedIndex = items.indexOfFirst { it.isSelected }

        // Deselect old
        if (oldSelectedIndex != -1) {
            items[oldSelectedIndex].isSelected = false
            updateItem(oldSelectedIndex, items[oldSelectedIndex])
        }
    }

}
