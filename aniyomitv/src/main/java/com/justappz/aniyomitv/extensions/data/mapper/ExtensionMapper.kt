package com.justappz.aniyomitv.extensions.data.mapper

import com.justappz.aniyomitv.extensions.data.dto.ExtensionDTO
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionEntity
import com.justappz.aniyomitv.extensions.domain.model.ExtensionDomain

fun ExtensionDTO.toEntity(repoUrl: String): ExtensionEntity {

    // Compute repoBase from JSON URL
    var base = run {
        val lastSlash = repoUrl.lastIndexOf('/')
        if (lastSlash != -1) repoUrl.substring(0, lastSlash + 1) else repoUrl
    }
    base = base.trimEnd('/')
    val fileUrl = base.let { "$it/apk/$apk" }
    val iconUrl = base.let { "$it/icon/$pkg.png" }

    return ExtensionEntity(
        repoUrl = repoUrl,
        apk = this.apk,
        code = this.code,
        lang = this.lang,
        name = this.name.replace("Aniyomi: ", ""),
        nsfw = this.nsfw,
        pkg = this.pkg,
        version = this.version,
        repoBase = base,
        fileUrl = fileUrl,
        iconUrl = iconUrl,
    )
}

fun ExtensionEntity.toDomain() = ExtensionDomain(
    apk = apk,
    code = code,
    lang = lang,
    name = name,
    nsfw = nsfw,
    pkg = pkg,
    version = version,
    repoBase = repoBase,
    fileUrl = fileUrl,
    iconUrl = iconUrl,
)
