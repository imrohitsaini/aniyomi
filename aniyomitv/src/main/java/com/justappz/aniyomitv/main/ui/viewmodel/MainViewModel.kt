package com.justappz.aniyomitv.main.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.constants.SimpleDateFormatConstants
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.domain.usecase.GetMainScreenTabsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    private val getMainScreenTabsUseCase: GetMainScreenTabsUseCase
) : ViewModel() {

    //region tabs
    private val _tabs = MutableLiveData<List<MainScreenTab>>()
    val tabs: LiveData<List<MainScreenTab>> = _tabs

    fun loadTabs() {
        _tabs.value = getMainScreenTabsUseCase()
    }
    //endregion

    //region time
    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime
    private var timeJob: Job? = null

    /**
     * This method updates time every minute
     * */
    // Start auto-updating time
    fun startUpdatingTime() {
        timeJob?.cancel() // cancel any previous job
        timeJob = viewModelScope.launch {
            while (true) {
                val sdf = SimpleDateFormat(SimpleDateFormatConstants.TIME_IN_TWELVE_HOUR_AM_PM, Locale.getDefault())
                _currentTime.value = sdf.format(Date())
                delay(1000) // wait for 1 minute
            }
        }
    }

    // stop updating time if needed
    fun stopUpdatingTime() {
        timeJob?.cancel()
    }
    //endregion
}
