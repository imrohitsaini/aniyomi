package com.justappz.aniyomitv.main.domain.usecase

import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.domain.repo.TabRepository

class GetMainScreenTabsUseCase(
    private val repository: TabRepository,
) {
    operator fun invoke(): List<MainScreenTab> {
        return repository.getTabs()
    }
}
