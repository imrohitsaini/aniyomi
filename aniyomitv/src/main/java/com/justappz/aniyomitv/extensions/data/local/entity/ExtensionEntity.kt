package com.justappz.aniyomitv.extensions.data.local.entity

import androidx.room.Entity
import com.justappz.aniyomitv.constants.RoomDBConstants

@Entity(
    tableName = RoomDBConstants.ENTITY_EXTENSIONS,
    primaryKeys = ["pkg", "repoUrl"],
)
data class ExtensionEntity(
    val repoUrl: String,  // nullable, because not all extensions must belong to a repo
    val apk: String,
    val code: Int,
    val lang: String,
    val name: String,
    val nsfw: Int,
    val pkg: String,
    val version: String,
    // Precomputed
    val repoBase: String?,
    val iconUrl: String?,
    val fileUrl: String?,
)
