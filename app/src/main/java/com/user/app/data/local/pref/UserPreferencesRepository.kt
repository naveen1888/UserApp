package com.user.app.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

/**
 * Repository for managing user preferences using Jetpack DataStore.
 * Marked as open to allow mocking in instrumented tests.
 */
@Singleton
open class UserPreferencesRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    /**
     * Flow that emits whether the user is currently logged in.
     */
    open val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }

    /**
     * Flow that emits the logged-in user's email.
     */
    open val userEmail: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_EMAIL]
        }

    /**
     * Updates the login status and user email in DataStore.
     * 
     * @param isLoggedIn The new login status
     * @param email The user's email
     */
    open suspend fun saveLoginStatus(isLoggedIn: Boolean, email: String?) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            if (email != null) {
                preferences[PreferencesKeys.USER_EMAIL] = email
            } else {
                preferences.remove(PreferencesKeys.USER_EMAIL)
            }
        }
    }
}
