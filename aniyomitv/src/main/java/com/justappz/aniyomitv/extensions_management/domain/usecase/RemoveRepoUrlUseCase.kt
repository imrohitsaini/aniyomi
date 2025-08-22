package com.justappz.aniyomitv.extensions_management.domain.usecase

import com.justappz.aniyomitv.extensions_management.domain.repo.RepoUrlRepo

class RemoveRepoUrlUseCase(
    private val repoUrlRepo: RepoUrlRepo
) {
    operator fun invoke(url: String) = repoUrlRepo.removeRepo(url)
}
