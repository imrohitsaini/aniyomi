package com.justappz.aniyomitv.core.util

import android.view.KeyEvent
import android.view.View

class FocusKeyHandler(
    private val onLeft: (() -> Unit)? = null,
    private val onRight: (() -> Unit)? = null,
    private val onUp: (() -> Unit)? = null,
    private val onDown: (() -> Unit)? = null
) : View.OnKeyListener {

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action != KeyEvent.ACTION_DOWN) return false

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> { onLeft?.invoke(); true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { onRight?.invoke(); true }
            KeyEvent.KEYCODE_DPAD_UP -> { onUp?.invoke(); true }
            KeyEvent.KEYCODE_DPAD_DOWN -> { onDown?.invoke(); true }
            else -> false
        }
    }
}
