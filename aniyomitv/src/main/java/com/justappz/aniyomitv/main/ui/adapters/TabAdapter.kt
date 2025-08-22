package com.justappz.aniyomitv.main.ui.adapters

import com.justappz.aniyomitv.base.BaseArrayObjectAdapter
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.ui.presenter.TabPresenter

class TabAdapter : BaseArrayObjectAdapter<MainScreenTab>(TabPresenter()) {

    fun setTabs(tabs: List<MainScreenTab>) {
        setItems(tabs)
    }

    fun selectTab(position: Int) {
        val items = getItems().toMutableList()

        // Find currently selected tab
        val oldSelectedIndex = items.indexOfFirst { it.isSelected }

        // Only update if selection actually changes
        if (oldSelectedIndex == position) return

        // Deselect old
        if (oldSelectedIndex != -1) {
            items[oldSelectedIndex].isSelected = false
            updateItem(oldSelectedIndex, items[oldSelectedIndex])
        }

        // Select new
        items[position].isSelected = true
        updateItem(position, items[position])
    }

    fun currentList(): List<MainScreenTab> = getItems()
}
