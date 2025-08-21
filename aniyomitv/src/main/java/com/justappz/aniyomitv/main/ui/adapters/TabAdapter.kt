package com.justappz.aniyomitv.main.ui.adapters

import androidx.core.content.ContextCompat
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.databinding.ItemTabBinding
import com.justappz.aniyomitv.main.domain.model.MainScreenTab

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
)
