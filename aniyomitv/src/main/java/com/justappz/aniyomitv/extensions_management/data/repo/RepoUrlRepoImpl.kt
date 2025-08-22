package com.justappz.aniyomitv.extensions_management.data.repo

import com.justappz.aniyomitv.constants.PreferenceKeys
import com.justappz.aniyomitv.extensions_management.domain.repo.RepoUrlRepo
import tachiyomi.core.common.preference.Preference
import tachiyomi.core.common.preference.PreferenceStore

class RepoUrlRepoImpl(
    preferenceStore: PreferenceStore
) : RepoUrlRepo {

    private val repoUrlsPref: Preference<Set<String>> =
        preferenceStore.getStringSet(PreferenceKeys.PREF_REPO_URLS, emptySet())

    override fun getRepos(): List<String> =
        repoUrlsPref.get().toList()

    override fun addRepo(url: String) {
        val current = repoUrlsPref.get().toMutableSet()
        current.add(url)
        repoUrlsPref.set(current)
    }

    override fun removeRepo(url: String) {
        val current = repoUrlsPref.get().toMutableSet()
        current.remove(url)
        repoUrlsPref.set(current)
    }

    override fun clearRepos() {
        repoUrlsPref.set(emptySet())
    }
}
