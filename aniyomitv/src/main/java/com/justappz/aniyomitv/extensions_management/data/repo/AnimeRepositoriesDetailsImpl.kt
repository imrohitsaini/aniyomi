package com.justappz.aniyomitv.extensions_management.data.repo

import com.justappz.aniyomitv.constants.PreferenceKeys
import com.justappz.aniyomitv.core.util.toJson
import com.justappz.aniyomitv.core.util.toObject
import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.AnimeRepositoriesDetailsRepo
import tachiyomi.core.common.preference.Preference
import tachiyomi.core.common.preference.PreferenceStore

class AnimeRepositoriesDetailsImpl(
    preferenceStore: PreferenceStore,
) : AnimeRepositoriesDetailsRepo {

    // Store JSON strings in a Set
    private val repoUrlsPref: Preference<Set<String>> =
        preferenceStore.getStringSet(PreferenceKeys.PREF_REPO_URLS, emptySet())

    override fun getRepos(): List<AnimeRepositoriesDetailsDomain> {
        return repoUrlsPref.get()
            .map { it.toObject<AnimeRepositoriesDetailsDomain>() }
            .sortedByDescending { it.dateAdded } // newest first
    }

    override fun addRepo(animeRepoDetail: AnimeRepositoriesDetailsDomain) {
        val current = repoUrlsPref.get().toMutableSet()
        current.add(animeRepoDetail.toJson())
        repoUrlsPref.set(current)
    }

    override fun removeRepo(animeRepoDetail: AnimeRepositoriesDetailsDomain) {
        val current = repoUrlsPref.get().toMutableSet()
        // Remove by matching JSON string
        current.remove(animeRepoDetail.toJson())
        repoUrlsPref.set(current)
    }

    override fun clearRepos() {
        repoUrlsPref.set(emptySet())
    }
}
