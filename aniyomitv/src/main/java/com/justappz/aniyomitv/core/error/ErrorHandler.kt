package com.justappz.aniyomitv.core.error

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast

object ErrorHandler {
    fun show(context: Context, error: AppError, inlineView: TextView? = null) {
        when (error.displayType) {
            ErrorDisplayType.TOAST -> {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
            ErrorDisplayType.INLINE -> {
                inlineView?.apply {
                    text = error.message
                    visibility = View.VISIBLE
                } ?: Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
            ErrorDisplayType.DIALOG -> {
                AlertDialog.Builder(context)
                    .setMessage(error.message)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
