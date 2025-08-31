package com.justappz.aniyomitv.core.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PrefsManager {

    private const val PREFS_NAME = "app_prefs"
    private lateinit var prefs: SharedPreferences

    // Call once in Application class or BaseActivity
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Save String
    fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String, default: String? = null): String? {
        return prefs.getString(key, default)
    }

    // Save Int
    fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun getInt(key: String, default: Int = 0): Int {
        return prefs.getInt(key, default)
    }

    // Save Boolean
    fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    // Save Long
    fun putLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return prefs.getLong(key, default)
    }

    // Save Float
    fun putFloat(key: String, value: Float) {
        prefs.edit { putFloat(key, value) }
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        return prefs.getFloat(key, default)
    }

    // Remove specific key
    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    // Clear all prefs
    fun clear() {
        prefs.edit { clear() }
    }
}
