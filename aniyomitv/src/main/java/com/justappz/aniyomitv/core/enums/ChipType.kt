package com.justappz.aniyomitv.core.enums

import androidx.annotation.DrawableRes
import com.justappz.aniyomitv.R

enum class ChipType(@DrawableRes val backgroundRes: Int) {
    CHIP_BUTTON(R.drawable.chips_button_bg),
    FILTER(R.drawable.chip_filter_bg)
}
