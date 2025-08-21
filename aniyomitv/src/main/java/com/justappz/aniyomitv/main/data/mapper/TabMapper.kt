package com.justappz.aniyomitv.main.data.mapper

import com.justappz.aniyomitv.main.data.dto.MainScreenTabData
import com.justappz.aniyomitv.main.domain.model.MainScreenTab

fun MainScreenTabData.toDomain(): MainScreenTab {
    return MainScreenTab(
        index = this.index,
        title = this.title,
        isSelected = this.isSelected,
        iconRes = this.iconRes,
        fragment = this.fragment,
        tag = this.fragmentTag
    )
}
