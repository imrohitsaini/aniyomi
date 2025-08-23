package com.justappz.aniyomitv.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity

/**
 * BaseActivity is a simple base class for all activities in the application.
 * It extends Activity to provide basic functionality and can be extended
 * by other activities to inherit common behavior.
 * */
open class BaseActivity : FragmentActivity() {

    //region variables
    protected val ctx: Context get() = this
    protected val act: Activity get() = this
    //endregion

    //region onCreate
    /**
     * It initializes the activity and sets up any necessary components.
     * Fix rotation to landscape
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    //endregion
}
