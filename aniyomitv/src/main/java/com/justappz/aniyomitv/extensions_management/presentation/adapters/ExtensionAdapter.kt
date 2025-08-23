package com.justappz.aniyomitv.extensions_management.presentation.adapters

import android.annotation.SuppressLint
import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.base.BasePagingAdapter
import com.justappz.aniyomitv.databinding.ItemExtensionBinding
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain

@SuppressLint("SetTextI18n")
class ExtensionPagingAdapter : BasePagingAdapter<ExtensionDomain, ItemExtensionBinding>(
    bindingInflater = { inflater, parent, attach ->
        ItemExtensionBinding.inflate(inflater, parent, attach)
    },
    bind = { item, _ ->
        tvAppName.text = item.name.replace("Aniyomi: ", "")
        tvVersion.text = "${item.version} ${item.code}"

        try {
            ivAppIcon.load(item.iconUrl) {
                crossfade(true) // default while loading // fallback if fails
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    },
    diffCallback = ExtensionDomain.DIFF_CALLBACK,
)
