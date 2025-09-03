package com.justappz.aniyomitv.playback.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.justappz.aniyomitv.constants.RoomDBConstants
import com.justappz.aniyomitv.playback.data.local.entity.AnimeEntity
import com.justappz.aniyomitv.playback.data.local.entity.EpisodeEntity

@Dao
interface AnimeEpisodeDao {

    /**
     * Inserts a new Anime into the database.
     *
     * - If the Anime does not already exist, it will be inserted.
     * - If the Anime already exists (based on the primary key), it will be updated.
     *
     * @param anime The [AnimeEntity] to insert or update.
     */
    @Upsert
    suspend fun insertAnime(anime: AnimeEntity)

    /**
     * @param animeKey Find the anime in the db if exits
     * */
    @Query(
        "SELECT * FROM ${RoomDBConstants.ENTITY_ANIME} " +
            "WHERE animeKey = :animeKey ",
    )
    suspend fun getAnimeWithKey(animeKey: String): AnimeEntity?

    /**
     * Inserts or updates an Episode in the database.
     *
     * - If an Episode with the same primary key exists, it will be replaced.
     * - Otherwise, the new Episode will be inserted.
     *
     * @param episode The [EpisodeEntity] to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)

    /**
     * Retrieves all episodes for a given Anime, ordered by episode number in ascending order.
     *
     * @param animeUrl The unique identifier (URL) of the Anime.
     * @return A [List] of [EpisodeEntity] objects corresponding to the Anime.
     */
    @Query(
        "SELECT * FROM ${RoomDBConstants.ENTITY_EPISODES} " +
            "WHERE animeUrl = :animeUrl " +
            "ORDER BY episodeNumber ASC",
    )
    suspend fun getEpisodesForAnime(animeUrl: String): List<EpisodeEntity>
}
