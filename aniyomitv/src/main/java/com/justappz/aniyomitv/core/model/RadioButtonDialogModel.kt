package com.justappz.aniyomitv.core.model

data class RadioButtonDialogModel(
    val title: String,
    val description: String? = "",
    val options: List<Options>,
    val isDefaultSelected: Boolean? = false,
)

data class Options(
    val buttonTitle: String,
    var isSelected: Boolean? = false,
)
