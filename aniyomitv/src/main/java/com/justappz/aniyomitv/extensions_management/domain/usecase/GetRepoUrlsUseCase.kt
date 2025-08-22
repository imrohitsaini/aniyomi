package com.justappz.aniyomitv.extensions_management.domain.usecase

import com.justappz.aniyomitv.extensions_management.domain.repo.RepoUrlRepo

class GetRepoUrlsUseCase(
    private val repoUrlRepo: RepoUrlRepo
) {
    operator fun invoke(): List<String> = repoUrlRepo.getRepos()
}
