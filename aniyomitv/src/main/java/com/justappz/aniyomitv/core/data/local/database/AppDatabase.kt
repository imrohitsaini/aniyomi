package com.justappz.aniyomitv.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.justappz.aniyomitv.extensions.data.local.dao.ExtensionRepoDetailsDao
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionEntity
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionRepoDetailsEntity
import com.justappz.aniyomitv.playback.data.local.dao.AnimeEpisodeDao
import com.justappz.aniyomitv.playback.data.local.entity.AnimeEntity

@Database(
    entities = [ExtensionRepoDetailsEntity::class, ExtensionEntity::class, AnimeEntity::class],
    version = 2,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeRepositoryDao(): ExtensionRepoDetailsDao
    abstract fun animeEpisodeDao(): AnimeEpisodeDao
}
