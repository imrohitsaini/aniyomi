package com.justappz.aniyomitv.extensions.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions.domain.model.RepoDomain

interface ExtensionRepo {
    fun getExtensions(url: String): BaseUiState<RepoDomain>
}
