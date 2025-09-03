package com.justappz.aniyomitv.main.data.repo

import com.justappz.aniyomitv.main.data.dto.MainScreenTabData
import com.justappz.aniyomitv.main.data.mapper.toDomain
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.domain.repo.TabRepository
import com.justappz.aniyomitv.extensions.presentation.fragments.ExtensionFragment
import com.justappz.aniyomitv.library.presentation.fragment.LibraryFragment
import com.justappz.aniyomitv.search.presentation.fragments.SearchFragment

class TabRepositoryImpl : TabRepository {

    var tabDataList = listOf(
        MainScreenTabData(
            index = 0,
            title = "Library",
            isSelected = true,
            fragmentProvider = { LibraryFragment() },
            fragmentTag = "home_fragment"
        ),
        MainScreenTabData(
            index = 1,
            title = "Search",
            fragmentProvider = { SearchFragment() },
            fragmentTag = "search_fragment",
        ),
        MainScreenTabData(
            index = 3,
            title = "Extension",
            fragmentProvider = { ExtensionFragment() },
            fragmentTag = "extension_fragment"
        )
    )

    override fun getTabs(): List<MainScreenTab> = tabDataList.map { it.toDomain() }.sortedBy { it.index }
}
