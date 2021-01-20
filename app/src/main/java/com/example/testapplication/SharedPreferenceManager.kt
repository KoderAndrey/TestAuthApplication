package com.example.testapplication

import android.content.Context
import androidx.core.content.edit

class SharedPreferenceManager(context: Context) {

    companion object {
        private const val PREFERENCES = "preferences"
        private const val LOGIN = "LOGIN"
        private const val PASSWORD = "PASSWORD"
    }

    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun setLogin(login: String) {
        preferences.edit { putString(LOGIN, login) }
    }

    fun setPassword(login: String) {
        preferences.edit { putString(PASSWORD, login) }
    }

    fun getLogin() = preferences.getString(LOGIN, "")

    fun getPassword() = preferences.getString(PASSWORD, "")
}