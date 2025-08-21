package com.justappz.aniyomitv.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * BaseActivity is a simple base class for all activities in the application.
 * It extends AppCompatActivity to provide basic functionality and can be extended
 * by other activities to inherit common behavior.
 * */
open class BaseActivity : AppCompatActivity() {

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

    //region applyPaddingToMainView
    /**
     * applyPaddingToMainView is a utility function to apply padding to the main view
     * based on the system window insets. This is useful for ensuring that the content
     * is not obscured by system UI elements like the status bar or navigation bar.
     */
    protected fun applyPaddingToMainView(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
