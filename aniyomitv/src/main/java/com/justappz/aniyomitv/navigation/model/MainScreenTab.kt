package com.justappz.aniyomitv.navigation.model

data class MainScreenTab(
    val index: Int,
    val title: String,
    var isSelected: Boolean = false,
    val iconRes: Int? = null
)
