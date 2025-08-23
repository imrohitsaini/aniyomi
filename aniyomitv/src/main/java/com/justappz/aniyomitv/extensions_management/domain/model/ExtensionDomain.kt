package com.justappz.aniyomitv.extensions_management.domain.model

data class RepoDomain(
    val repoUrl: String,
    val extensions: List<ExtensionDomain>,
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
    var repoBase: String? = null
)  {
    private fun normalizeBase(): String? {
        return repoBase?.trimEnd('/') // remove trailing slash if present
    }

    val iconUrl: String?
        get() = normalizeBase()?.let { "$it/icon/$pkg.png" }

    val fileUrl: String?
        get() = normalizeBase()?.let { "$it/apk/$apk" }
}

data class Source(
    val baseUrl: String,
    val id: String,
    val lang: String,
    val name: String,
)
