package com.justappz.aniyomitv.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.justappz.aniyomitv.extensions_management.data.local.dao.ExtensionRepoDetailsDao
import com.justappz.aniyomitv.extensions_management.data.local.entity.ExtensionRepoDetailsEntity

@Database(
    entities = [ExtensionRepoDetailsEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeRepositoryDao(): ExtensionRepoDetailsDao
}
