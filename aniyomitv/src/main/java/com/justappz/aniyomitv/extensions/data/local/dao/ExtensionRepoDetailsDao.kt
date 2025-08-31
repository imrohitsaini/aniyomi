package com.justappz.aniyomitv.extensions.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.justappz.aniyomitv.constants.RoomDBConstants
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionEntity
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionRepoDetailsEntity

@Dao
interface ExtensionRepoDetailsDao {

    @Query("SELECT * FROM ${RoomDBConstants.ENTITY_REPO_DETAILS}  ORDER BY dateAdded DESC")
    suspend fun getRepos(): List<ExtensionRepoDetailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: ExtensionRepoDetailsEntity)

    // Extensions for a specific repo
    @Query("SELECT * FROM ${RoomDBConstants.ENTITY_EXTENSIONS} WHERE repoUrl = :repoUrl")
    suspend fun getExtensionsByRepo(repoUrl: String): List<ExtensionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExtensions(extensions: List<ExtensionEntity>)

}
