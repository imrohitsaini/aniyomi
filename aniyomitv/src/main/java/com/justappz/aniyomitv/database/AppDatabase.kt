package com.justappz.aniyomitv.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.justappz.aniyomitv.extensions.data.local.dao.ExtensionRepoDetailsDao
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionEntity
import com.justappz.aniyomitv.extensions.data.local.entity.ExtensionRepoDetailsEntity
import com.justappz.aniyomitv.database.dao.AnimeEpisodeDao
import com.justappz.aniyomitv.database.entity.AnimeEntity
import com.justappz.aniyomitv.database.entity.EpisodeEntity

@Database(
    entities = [
        ExtensionRepoDetailsEntity::class,
        ExtensionEntity::class,
        AnimeEntity::class,
        EpisodeEntity::class,
    ],
    version = 7,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeRepositoryDao(): ExtensionRepoDetailsDao
    abstract fun animeEpisodeDao(): AnimeEpisodeDao
}
