package com.justappz.aniyomitv.extensions.data.mapper

import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionRepoDetailsEntity
import com.justappz.aniyomitv.extensions.domain.model.ExtensionRepositoriesDetailsDomain

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
