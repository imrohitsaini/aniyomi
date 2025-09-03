package com.justappz.aniyomitv.playback.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.justappz.aniyomitv.constants.RoomDBConstants
import com.justappz.aniyomitv.playback.data.local.entity.AnimeEntity
import com.justappz.aniyomitv.playback.data.local.entity.EpisodeEntity

@Dao
interface AnimeEpisodeDao {

    // Insert a new Anime (returns generated id)
    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertAnime(anime: AnimeEntity)

    // Insert/update a new Episode
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)

    @Query(
        "SELECT * FROM ${
            RoomDBConstants.ENTITY_EPISODES
        } WHERE animeUrl = :animeUrl ORDER BY episodeNumber ASC",
    )
    suspend fun getEpisodesForAnime(animeUrl: String): List<EpisodeEntity>
}
