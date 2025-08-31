package com.firefinchdev.prmerge.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "pr_merge_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_GITHUB_TOKEN = "github_token"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    fun saveGitHubToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_GITHUB_TOKEN, token)
            .apply()
    }

    fun getGitHubToken(): String? {
        return sharedPreferences.getString(KEY_GITHUB_TOKEN, null)
    }

    fun clearGitHubToken() {
        sharedPreferences.edit()
            .remove(KEY_GITHUB_TOKEN)
            .apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
}