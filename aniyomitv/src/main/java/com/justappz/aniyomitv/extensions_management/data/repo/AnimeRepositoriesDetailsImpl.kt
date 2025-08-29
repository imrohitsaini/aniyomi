package com.justappz.aniyomitv.extensions_management.data.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.constants.PreferenceKeys
import com.justappz.aniyomitv.core.error.AppError
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

    override fun getRepos(): BaseUiState<List<AnimeRepositoriesDetailsDomain>> {
        return try {
            val repos = repoUrlsPref.get()
                .map { it.toObject<AnimeRepositoriesDetailsDomain>() }
                .sortedByDescending { it.dateAdded }

            if (repos.isEmpty()) {
                BaseUiState.Empty
            } else {
                BaseUiState.Success(repos)
            }
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.UnknownError(
                    message = e.message ?: "Failed to load repositories",
                ),
            )
        }
    }

    override fun addRepo(animeRepoDetail: AnimeRepositoriesDetailsDomain) {
        try {
            val current = repoUrlsPref.get().toMutableSet()
            current.add(animeRepoDetail.toJson())
            repoUrlsPref.set(current)
        } catch (e: Exception) {
            // log/report if needed
        }
    }
}
