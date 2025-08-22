package com.justappz.aniyomitv.main.ui.adapters

import androidx.core.content.ContextCompat
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.databinding.ItemTabBinding
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import android.view.View

class TabAdapter(
    tabs: List<MainScreenTab>,
) : BaseRecyclerViewAdapter<MainScreenTab, ItemTabBinding>(
    items = tabs,
    bindingInflater = ItemTabBinding::inflate,
    bind = { tab, binding ->
        tvTabTitle.text = tab.title
        root.isSelected = tab.isSelected

        if (tab.isSelected) {
            tabUnderline.setBackgroundColor(ContextCompat.getColor(root.context, R.color.anime_tv_primary))
        } else {
            tabUnderline.setBackgroundColor(ContextCompat.getColor(root.context, R.color.transparent))
        }
    },
) {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemTabBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        holder.binding.root.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // focused
                val tabs = getCurrentList()
                val lastSelectedIndex = tabs.indexOfFirst { it.isSelected }

                // Only update if clicked tab is not already selected
                if (lastSelectedIndex != position) {
                    // Deselect previous
                    if (lastSelectedIndex != -1) {
                        tabs[lastSelectedIndex].isSelected = false
                        notifyItemChanged(lastSelectedIndex)
                    }

                    // Select current
                    tabs[position].isSelected = true
                    notifyItemChanged(position)

                    // Invoke click listener to load the fragment on main activity
                    onItemClick?.invoke(tabs[position], position)
                }
            } else {
                // not focused
            }
        }

    }
}
