package com.justappz.aniyomitv.core.enums

import com.justappz.aniyomitv.R

enum class ButtonType(
    val backgroundRes: Int,
) {
    POSITIVE(backgroundRes = R.drawable.positive_button_bg),
    NEGATIVE(backgroundRes = R.drawable.negative_button_bg),
    DESTRUCTIVE(backgroundRes = R.drawable.destructive_button_bg),
}
