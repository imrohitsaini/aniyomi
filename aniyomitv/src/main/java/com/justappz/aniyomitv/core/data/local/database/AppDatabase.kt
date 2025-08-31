package com.justappz.aniyomitv.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.justappz.aniyomitv.extensions.data.local.dao.ExtensionRepoDetailsDao
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionEntity
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionRepoDetailsEntity

@Database(
    entities = [ExtensionRepoDetailsEntity::class, ExtensionEntity::class,],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeRepositoryDao(): ExtensionRepoDetailsDao
}
