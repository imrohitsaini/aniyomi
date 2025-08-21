package com.justappz.aniyomitv.main.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.domain.usecase.GetMainScreenTabsUseCase

class MainViewModel(
    private val getMainScreenTabsUseCase: GetMainScreenTabsUseCase
) : ViewModel() {

    private val _tabs = MutableLiveData<List<MainScreenTab>>()
    val tabs: LiveData<List<MainScreenTab>> = _tabs

    fun loadTabs() {
        _tabs.value = getMainScreenTabsUseCase()
    }
}
