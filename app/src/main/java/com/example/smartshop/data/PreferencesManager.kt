package com.example.smartshop.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "SmartShopPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_SAVED_EMAIL, email).apply()
    }

    fun getSavedEmail(): String? {
        return prefs.getString(KEY_SAVED_EMAIL, null)
    }

    fun clearSavedEmail() {
        prefs.edit().remove(KEY_SAVED_EMAIL).apply()
    }
}

