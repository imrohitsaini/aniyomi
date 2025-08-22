package com.justappz.aniyomitv.extensions_management.presentation.adapters

import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.core.util.UrlUtils
import com.justappz.aniyomitv.databinding.ItemRepoChipBinding

class RepoChipsAdapter(
    items: List<String> = listOf(),
) : BaseRecyclerViewAdapter<String, ItemRepoChipBinding>(
    items,
    ItemRepoChipBinding::inflate,
    { repoUrl, _ ->
        chipFilter.setText(UrlUtils.getCleanUrl(repoUrl))
    },
)
