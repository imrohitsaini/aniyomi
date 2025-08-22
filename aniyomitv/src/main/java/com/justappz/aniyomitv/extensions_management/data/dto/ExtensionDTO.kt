package com.justappz.aniyomitv.extensions_management.data.dto

data class ExtensionDTO(
    val apk: String,
    val code: Int,
    val lang: String,
    val name: String,
    val nsfw: Int,
    val pkg: String,
    val sources: List<SourceDTO>,
    val version: String,
)

data class SourceDTO(
    val baseUrl: String,
    val id: String,
    val lang: String,
    val name: String,
)
