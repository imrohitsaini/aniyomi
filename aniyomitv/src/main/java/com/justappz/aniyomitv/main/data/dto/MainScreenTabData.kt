package com.justappz.aniyomitv.main.data.dto

import androidx.fragment.app.Fragment

data class MainScreenTabData(
    val index: Int,
    val title: String,
    var isSelected: Boolean = false,
    val iconRes: Int? = null,
    val fragment: Fragment,
)
