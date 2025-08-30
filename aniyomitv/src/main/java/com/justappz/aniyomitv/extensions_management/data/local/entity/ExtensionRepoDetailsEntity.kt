package com.justappz.aniyomitv.extensions_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.justappz.aniyomitv.constants.RoomDBConstants

@Entity(tableName = RoomDBConstants.ENTITY_REPO_DETAILS)
data class ExtensionRepoDetailsEntity(
    @PrimaryKey val repoUrl: String,
    val dateAdded: Long,
    val name: String,
)
