package com.justappz.aniyomitv.extensions_management.data.mapper

import com.justappz.aniyomitv.extensions_management.data.dto.ExtensionDTO
import com.justappz.aniyomitv.extensions_management.data.dto.SourceDTO
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain
import com.justappz.aniyomitv.extensions_management.domain.model.Source

fun ExtensionDTO.toDomain(): ExtensionDomain {
    return ExtensionDomain(
        apk = this.apk,
        code = this.code,
        lang = this.lang,
        name = this.name,
        nsfw = this.nsfw,
        pkg = this.pkg,
        sources = this.sources.map { it.toDomain() },
        version = this.version,
    )
}

fun SourceDTO.toDomain(): Source {
    return Source(
        baseUrl = this.baseUrl,
        id = this.id,
        lang = this.lang,
        name = this.name,
    )
}
