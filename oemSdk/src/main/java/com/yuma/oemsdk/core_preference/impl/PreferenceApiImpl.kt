package com.yumaoem.corepreference.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.yumaoem.corepreference.api.PreferenceApi
import com.yumaoem.corepreference.impl.util.toBooleanPreferencesKey
import com.yumaoem.corepreference.impl.util.toDoublePreferencesKey
import com.yumaoem.corepreference.impl.util.toFloatPreferencesKey
import com.yumaoem.corepreference.impl.util.toIntPreferencesKey
import com.yumaoem.corepreference.impl.util.toLongPreferencesKey
import com.yumaoem.corepreference.impl.util.toStringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceApiImpl(
    private val dataStore: DataStore<Preferences>
) : PreferenceApi {
    override suspend fun getString(key: String): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[key.toStringPreferencesKey()].orEmpty()
        }
    }

    override suspend fun getString(key: String, default: String): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[key.toStringPreferencesKey()]?: default
        }
    }

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { settings->
            settings[key.toStringPreferencesKey()] = value
        }
    }

    override suspend fun getInt(key: String): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[key.toIntPreferencesKey()]?:0
        }
    }

    override suspend fun getInt(key: String, default: Int): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[key.toIntPreferencesKey()]?:default
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        dataStore.edit { settings->
            settings[key.toIntPreferencesKey()] = value
        }
    }

    override suspend fun getLong(key: String): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[key.toLongPreferencesKey()]?:0
        }
    }

    override suspend fun getLong(key: String, default: Long): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[key.toLongPreferencesKey()]?:default
        }
    }

    override suspend fun putLong(key: String, value: Long) {
        dataStore.edit { settings->
            settings[key.toLongPreferencesKey()] = value
        }
    }

    override suspend fun getFloat(key: String): Flow<Float> {
        return dataStore.data.map { preferences ->
            preferences[key.toFloatPreferencesKey()]?:0F
        }
    }

    override suspend fun getFloat(key: String, default: Float): Flow<Float> {
        return dataStore.data.map { preferences ->
            preferences[key.toFloatPreferencesKey()]?:default
        }
    }

    override suspend fun putFloat(key: String, value: Float) {
        dataStore.edit { settings->
            settings[key.toFloatPreferencesKey()] = value
        }
    }

    override suspend fun getDouble(key: String): Flow<Double> {
        return dataStore.data.map { preferences ->
            preferences[key.toDoublePreferencesKey()]?:0.0
        }
    }

    override suspend fun getDouble(key: String, default: Double): Flow<Double> {
        return dataStore.data.map { preferences ->
            preferences[key.toDoublePreferencesKey()]?:default
        }
    }

    override suspend fun putDouble(key: String, value: Double) {
        dataStore.edit { settings->
            settings[key.toDoublePreferencesKey()] = value
        }
    }

    override suspend fun getBoolean(key: String): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[key.toBooleanPreferencesKey()]?:false
        }
    }

    override suspend fun getBoolean(key: String, default: Boolean): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[key.toBooleanPreferencesKey()]?:default
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { settings->
            settings[key.toBooleanPreferencesKey()] = value
        }
    }

    override suspend fun removeString(key: String) {
        dataStore.edit { settings->
            settings.remove(key.toStringPreferencesKey())
        }
    }

    override suspend fun removeBoolean(key: String) {
        dataStore.edit { settings->
            settings.remove(key.toBooleanPreferencesKey())
        }
    }

    override suspend fun removeInt(key: String) {
        dataStore.edit { settings->
            settings.remove(key.toIntPreferencesKey())
        }
    }

    override suspend fun removeLong(key: String) {
        dataStore.edit { settings->
            settings.remove(key.toLongPreferencesKey())
        }
    }

    override suspend fun removeFloat(key: String) {
        dataStore.edit { settings->
            settings.remove(key.toFloatPreferencesKey())
        }
    }

    override suspend fun removeDouble(key: String) {
        dataStore.edit { settings->
            settings.remove(key.toDoublePreferencesKey())
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { settings->
            settings.clear()
        }
    }

}