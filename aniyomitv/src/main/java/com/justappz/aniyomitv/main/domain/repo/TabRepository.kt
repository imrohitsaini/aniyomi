package com.justappz.aniyomitv.main.domain.repo

import com.justappz.aniyomitv.main.domain.model.MainScreenTab

interface TabRepository {
    fun getTabs(): List<MainScreenTab>
}
