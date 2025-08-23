package com.justappz.aniyomitv.extensions_management.domain.usecase

import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.AnimeRepositoriesDetailsRepo

class SaveRepoUrlUseCase(
    private val animeRepositoriesDetailsRepo: AnimeRepositoriesDetailsRepo,
) {
    operator fun invoke(animeRepoDetail: AnimeRepositoriesDetailsDomain) =
        animeRepositoriesDetailsRepo.addRepo(animeRepoDetail)
}
