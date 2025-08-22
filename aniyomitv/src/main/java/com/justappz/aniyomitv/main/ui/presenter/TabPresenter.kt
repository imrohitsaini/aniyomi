package com.justappz.aniyomitv.main.ui.presenter

import com.justappz.aniyomitv.base.BasePresenter
import com.justappz.aniyomitv.databinding.ItemTabBinding
import com.justappz.aniyomitv.main.domain.model.MainScreenTab

class TabPresenter : BasePresenter<MainScreenTab, ItemTabBinding>(ItemTabBinding::inflate) {

    override fun bind(binding: ItemTabBinding, item: MainScreenTab) {
        binding.tvTabTitle.text = item.title
        binding.root.isSelected = item.isSelected
    }
}
