package com.justappz.aniyomitv.extensions_management.domain.repo

interface RepoUrlRepo {
    fun getRepos(): List<String>
    fun addRepo(url: String)
    fun removeRepo(url: String)
    fun clearRepos()
}
