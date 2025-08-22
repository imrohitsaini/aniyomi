package com.justappz.aniyomitv.extensions_management.domain.model

data class RepoDomain(
    val repoUrl: String,
    val extensions: List<ExtensionDomain>
)

data class ExtensionDomain(
    val apk: String,
    val code: Int,
    val lang: String,
    val name: String,
    val nsfw: Int,
    val pkg: String,
    val sources: List<Source>,
    val version: String,
)

data class Source(
    val baseUrl: String,
    val id: String,
    val lang: String,
    val name: String,
)
