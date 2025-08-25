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
    val repoBase: String? = null,
    val installedExtensionInfo: InstalledExtensionInfo? = null
) {
    private fun normalizeBase(): String? {
        return repoBase?.trimEnd('/') // remove trailing slash if present
    }

    val iconUrl: String?
        get() = normalizeBase()?.let { "$it/icon/$pkg.png" }

    val fileUrl: String?
        get() = normalizeBase()?.let { "$it/apk/$apk" }

    companion object {
        val DIFF_CALLBACK = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<ExtensionDomain>() {
            override fun areItemsTheSame(oldItem: ExtensionDomain, newItem: ExtensionDomain) =
                oldItem.pkg == newItem.pkg

            override fun areContentsTheSame(oldItem: ExtensionDomain, newItem: ExtensionDomain) =
                oldItem == newItem
        }
    }

    /**
     * Check if an update is required for this extension.
     * @return true if installed and repo versionCode is greater than installed versionCode
     */
    fun isUpdateRequired(): Boolean {
        val installedInfo = installedExtensionInfo
        return installedInfo?.installed == true &&
            (installedInfo.installedVersionCode ?: 0) < code
    }

}

data class InstalledExtensionInfo(
    var installed: Boolean? = false,
    var installedVersionCode: Int? = 0,
    var installedVersionName: String? = "",
)

data class Source(
    val baseUrl: String,
    val id: String,
    val lang: String,
    val name: String,
)
