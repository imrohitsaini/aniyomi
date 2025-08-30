package com.justappz.aniyomitv.extensions_management.data.mapper

import com.justappz.aniyomitv.extensions_management.data.local.entity.ExtensionRepoDetailsEntity
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionRepositoriesDetailsDomain

fun ExtensionRepoDetailsEntity.toDomain() = ExtensionRepositoriesDetailsDomain(
    repoUrl = repoUrl,
    dateAdded = dateAdded,
    cleanName = name,
)

fun ExtensionRepositoriesDetailsDomain.toEntity() = ExtensionRepoDetailsEntity(
    repoUrl = repoUrl,
    dateAdded = dateAdded,
    name = cleanName,
)
