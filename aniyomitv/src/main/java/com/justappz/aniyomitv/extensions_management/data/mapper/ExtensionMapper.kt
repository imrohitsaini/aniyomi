package com.justappz.aniyomitv.extensions_management.data.mapper

import com.justappz.aniyomitv.extensions_management.data.dto.ExtensionDTO
import com.justappz.aniyomitv.extensions_management.data.dto.SourceDTO
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain
import com.justappz.aniyomitv.extensions_management.domain.model.Source

fun ExtensionDTO.toDomain(repoUrl: String): ExtensionDomain {

    // Compute repoBase from JSON URL
    val base = run {
        val lastSlash = repoUrl.lastIndexOf('/')
        if (lastSlash != -1) repoUrl.substring(0, lastSlash + 1) else repoUrl
    }

    return ExtensionDomain(
        apk = this.apk,
        code = this.code,
        lang = this.lang,
        name = this.name,
        nsfw = this.nsfw,
        pkg = this.pkg,
        sources = this.sources.map { it.toDomain() },
        version = this.version,
        repoBase = base
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
