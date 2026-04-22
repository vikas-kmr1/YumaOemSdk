package com.yuma.oemsdk.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yuma_sdk_prefs")

/**
 * SDK-internal preference manager built on DataStore.
 *
 * Replaces [YumaPrefUtilApi] from the OEM app without requiring any DI framework.
 * Not exposed to the host application.
 */
internal class SdkPrefManager(private val context: Context) {

    private val dataStore = context.dataStore

    // ─── Keys ────────────────────────────────────────────────────────────────
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("sdk_access_token")
        val REFRESH_TOKEN = stringPreferencesKey("sdk_refresh_token")
        val USER_ID = stringPreferencesKey("sdk_user_id")
        val USER_DATA = stringPreferencesKey("sdk_user_data")
        val BOOKED_TOKEN = stringPreferencesKey("sdk_booked_token")
        val FCM_TOKEN = stringPreferencesKey("sdk_fcm_token")
        val IS_B2C = booleanPreferencesKey("sdk_is_b2c")
        val LAST_STATION_CACHE_TS = longPreferencesKey("sdk_last_station_cache_ts")
        val SUPPORT_PHONE = longPreferencesKey("sdk_support_phone")
        val SUPPORT_MESSAGE = stringPreferencesKey("sdk_support_message")
    }

    // ─── Auth Tokens ──────────────────────────────────────────────────────────
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? =
        dataStore.data.map { it[Keys.ACCESS_TOKEN] }.first()

    suspend fun getRefreshToken(): String? =
        dataStore.data.map { it[Keys.REFRESH_TOKEN] }.first()

    fun clearTokens() {
        kotlinx.coroutines.runBlocking {
            dataStore.edit { prefs ->
                prefs.remove(Keys.ACCESS_TOKEN)
                prefs.remove(Keys.REFRESH_TOKEN)
            }
        }
    }

    // ─── User ─────────────────────────────────────────────────────────────────
    suspend fun saveUserData(json: String) {
        dataStore.edit { it[Keys.USER_DATA] = json }
    }

    suspend fun getUserData(): String? =
        dataStore.data.map { it[Keys.USER_DATA] }.first()

    suspend fun getUserId(): String? =
        dataStore.data.map { it[Keys.USER_ID] }.first()

    suspend fun saveUserId(userId: String) {
        dataStore.edit { it[Keys.USER_ID] = userId }
    }

    // ─── Booked Token ─────────────────────────────────────────────────────────
    suspend fun saveBookedToken(json: String) {
        dataStore.edit { it[Keys.BOOKED_TOKEN] = json }
    }

    suspend fun getBookedToken(): String? =
        dataStore.data.map { it[Keys.BOOKED_TOKEN] }.first()

    suspend fun clearBookedToken() {
        dataStore.edit { it.remove(Keys.BOOKED_TOKEN) }
    }

    // ─── FCM Token ────────────────────────────────────────────────────────────
    fun getFcmTokenFlow(): Flow<String?> =
        dataStore.data.map { it[Keys.FCM_TOKEN] }

    suspend fun saveFcmToken(token: String) {
        dataStore.edit { it[Keys.FCM_TOKEN] = token }
    }

    // ─── B2C Status ───────────────────────────────────────────────────────────
    fun getB2cStatusFlow(): Flow<Boolean> =
        dataStore.data.map { it[Keys.IS_B2C] ?: false }

    suspend fun setB2cStatus(isB2c: Boolean) {
        dataStore.edit { it[Keys.IS_B2C] = isB2c }
    }

    // ─── Station Cache Timestamp ──────────────────────────────────────────────
    suspend fun getLastCachedStationTimestamp(): Long =
        dataStore.data.map { it[Keys.LAST_STATION_CACHE_TS] ?: 0L }.first()

    suspend fun saveLastCachedStationTimestamp(ts: Long) {
        dataStore.edit { it[Keys.LAST_STATION_CACHE_TS] = ts }
    }

    // ─── Support Details ──────────────────────────────────────────────────────
    suspend fun saveSupportDetails(phone: Long, message: String) {
        dataStore.edit {
            it[Keys.SUPPORT_PHONE] = phone
            it[Keys.SUPPORT_MESSAGE] = message
        }
    }

    fun getSupportDetailsFlow(): Flow<Pair<Long?, String?>> =
        dataStore.data.map { prefs ->
            prefs[Keys.SUPPORT_PHONE] to prefs[Keys.SUPPORT_MESSAGE]
        }

    // ─── Full Logout ──────────────────────────────────────────────────────────
    suspend fun logoutUser() {
        dataStore.edit { it.clear() }
    }

    suspend fun isLoggedIn(): Boolean =
        getAccessToken()?.isNotBlank() == true
}
