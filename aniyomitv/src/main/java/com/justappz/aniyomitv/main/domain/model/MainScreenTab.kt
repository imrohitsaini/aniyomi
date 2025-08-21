package com.justappz.aniyomitv.main.domain.model

import androidx.fragment.app.Fragment

data class MainScreenTab(
    val index: Int,
    val title: String,
    var isSelected: Boolean = false,
    val iconRes: Int? = null,
    val fragment: Fragment
)
