package com.justappz.aniyomitv.core.util

import android.view.KeyEvent
import android.view.View

class FocusKeyHandler(
    private val onLeft: (() -> Boolean)? = null,
    private val onRight: (() -> Boolean)? = null,
    private val onUp: (() -> Boolean)? = null,
    private val onDown: (() -> Boolean)? = null,
    private val onCenter: (() -> Boolean)? = null
) : View.OnKeyListener {

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action != KeyEvent.ACTION_DOWN) return false

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> onLeft?.invoke() ?: false
            KeyEvent.KEYCODE_DPAD_RIGHT -> onRight?.invoke() ?: false
            KeyEvent.KEYCODE_DPAD_UP -> onUp?.invoke() ?: false
            KeyEvent.KEYCODE_DPAD_DOWN -> onDown?.invoke() ?: false
            KeyEvent.KEYCODE_DPAD_CENTER -> onCenter?.invoke() ?: false
            else -> false
        }
    }
}
