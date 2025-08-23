package com.justappz.aniyomitv.extensions_management.presentation.adapters

import android.util.Log
import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.databinding.ItemRepoChipBinding
import com.justappz.aniyomitv.extensions_management.domain.model.Chip

class RepoChipsAdapter(
    items: List<Chip> = listOf(),
) : BaseRecyclerViewAdapter<Chip, ItemRepoChipBinding>(
    items,
    ItemRepoChipBinding::inflate,
    { chip, _ ->
        chipFilter.setText(chip.chipName)
        chipFilter.setChipIcon(chip.chipIcon)
        chipFilter.setSelectedState(chip.isSelected)
    },
)
