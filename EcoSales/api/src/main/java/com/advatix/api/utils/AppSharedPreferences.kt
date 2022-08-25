package com.advatix.lepapi.utils

import android.content.Context
import com.advatix.lepapi.utils.AppConstants.Preferences.PREFS_NAME

public class AppSharedPreferences {
    fun setPreferencesString(
        context: Context?,
        key: String?,
        value: String?
    ) {
        if (context == null) return
        val editor =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setPreferenceBoolean(
        context: Context?,
        key: String?,
        value: Boolean
    ) {
        if (context == null) return
        val editor =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPreferencesString(
        context: Context?,
        key: String?
    ): String? {
        if (context == null) return null
        val prefs =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, "")
    }

    fun setPreferenceInt(
        context: Context?,
        key: String?,
        value: Int
    ) {
        if (context == null) return
        val editor =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getPreferencesInt(context: Context?, key: String?): Int {
        if (context == null) return 0
        val prefs =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(key, 0)
    }

    fun setPreferenceLong(
        context: Context?,
        key: String?,
        value: Long
    ) {
        if (context == null) return
        val editor =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getPreferencesLong(context: Context?, key: String?): Long {
        if (context == null) return 0
        val prefs =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(key, 0)
    }

    fun getPreferenceBoolean(
        _context: Context?,
        key: String?
    ): Boolean {
        if (_context == null) return false
        val prefs =
            _context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }

    fun getRecentList(context: Context?, key: String?): String? {
        if (context == null) return null
        val prefs =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, "")
    }

    fun setRecentList(
        context: Context?,
        data: String?,
        key: String?
    ) {
        if (context == null) return
        val editor =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(key, data)
        editor.apply()
    }

    fun clearPrefs(context: Context) {
        val preferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun removePrefs(context: Context, key: String) {
        val preferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(key)
        editor.apply()
    }
}