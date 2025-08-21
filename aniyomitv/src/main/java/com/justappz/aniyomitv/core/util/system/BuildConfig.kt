@file:Suppress("UNUSED", "KotlinConstantConditions")

package com.justappz.aniyomitv.core.util.system

import com.justappz.aniyomitv.BuildConfig

val isDebugBuildType: Boolean
    inline get() = BuildConfig.BUILD_TYPE == "debug"

val isPreviewBuildType: Boolean
    inline get() = BuildConfig.BUILD_TYPE == "preview"

val isReleaseBuildType: Boolean
    inline get() = BuildConfig.BUILD_TYPE == "release"
