package com.justappz.aniyomitv.extensions.presentation.adapters

import androidx.recyclerview.widget.RecyclerView
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.core.util.setMarginStartRes
import com.justappz.aniyomitv.databinding.ItemRepoChipBinding
import com.justappz.aniyomitv.extensions.domain.model.Chip

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

    fun getSelectedRepoPosition() = getCurrentList().indexOfFirst { it.isSelected }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemRepoChipBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        if (position == 0) {
            holder.binding.chipFilter.setMarginStartRes(R.dimen._20dp)
        } else {
            holder.binding.chipFilter.setMarginStartRes(R.dimen._8dp)
        }

    }
}
