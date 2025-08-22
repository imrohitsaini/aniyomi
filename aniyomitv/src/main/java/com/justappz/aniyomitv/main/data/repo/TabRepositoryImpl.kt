package com.justappz.aniyomitv.main.data.repo

import com.justappz.aniyomitv.main.data.dto.MainScreenTabData
import com.justappz.aniyomitv.main.data.mapper.toDomain
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.domain.repo.TabRepository
import com.justappz.aniyomitv.extensions_management.presentation.fragments.ExtensionFragment
import com.justappz.aniyomitv.main.ui.fragments.HomeFragment
import com.justappz.aniyomitv.main.ui.fragments.SearchFragment
import com.justappz.aniyomitv.main.ui.fragments.WatchListFragment

class TabRepositoryImpl : TabRepository {

    var tabDataList = listOf(
        MainScreenTabData(
            index = 0,
            title = "Home",
            isSelected = true,
            fragment = HomeFragment(),
            fragmentTag = "home_fragment"
        ),
        MainScreenTabData(
            index = 1,
            title = "Search",
            fragment = SearchFragment(),
            fragmentTag = "search_fragment",
        ),
        MainScreenTabData(
            index = 2,
            title = "Watchlist",
            fragment = WatchListFragment(),
            fragmentTag = "watchlist_fragment"
        ),
        MainScreenTabData(
            index = 3,
            title = "Extension",
            fragment = ExtensionFragment(),
            fragmentTag = "extension_fragment"
        )
    )

    override fun getTabs(): List<MainScreenTab> = tabDataList.map { it.toDomain() }.sortedBy { it.index }
}
