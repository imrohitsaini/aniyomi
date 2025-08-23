package com.justappz.aniyomitv.extensions_management.domain.model

data class Chip(
    val url: String,
    val chipName: String,
    var isSelected: Boolean = false,
    var chipIcon: Int? = null,
    var isAddRepoChip: Boolean = false,
)
