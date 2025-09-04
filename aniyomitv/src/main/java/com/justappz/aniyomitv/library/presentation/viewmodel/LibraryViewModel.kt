package com.justappz.aniyomitv.library.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.library.domain.usecase.GetAnimeInLibraryUseCase
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val getAnimeInLibraryUseCase: GetAnimeInLibraryUseCase,
) : ViewModel() {

    //region get anime in library
    private val _animeDomain = MutableStateFlow<BaseUiState<List<AnimeDomain>>>(BaseUiState.Idle)
    val animeDomain: StateFlow<BaseUiState<List<AnimeDomain>>> = _animeDomain.asStateFlow()

    fun getAnimeInLibrary() {
        viewModelScope.launch {
            _animeDomain.value = BaseUiState.Loading
            _animeDomain.value = getAnimeInLibraryUseCase()
        }
    }

    fun resetState() {
        _animeDomain.value = BaseUiState.Idle
    }
    //endregion
}
