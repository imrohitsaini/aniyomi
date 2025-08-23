package com.justappz.aniyomitv.extensions_management.presentation.adapters

import androidx.recyclerview.widget.RecyclerView
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
) {

    fun selectChip(chip: Chip, position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        // Deselect the previously selected chip
        getCurrentList().indexOfFirst { it.isSelected }.takeIf { it != -1 }?.let { index ->
            getItem(index).apply { isSelected = false }
                .also { updateItemAt(index, it) }
        }

        // Select the clicked chip
        chip.isSelected = true
        updateItemAt(position, chip)
    }
}
