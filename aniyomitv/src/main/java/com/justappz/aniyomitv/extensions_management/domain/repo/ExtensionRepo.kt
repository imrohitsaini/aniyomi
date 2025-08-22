package com.justappz.aniyomitv.extensions_management.domain.repo

import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain

interface ExtensionRepo {
    fun getExtensions(url: String): List<ExtensionDomain>
}
