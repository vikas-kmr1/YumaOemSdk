package com.yumaoem.corepreference.impl.util

import com.yumaoem.core.model.auth.BearerTokens
import com.yumaoem.core.model.auth.User
import com.yumaoem.corepreference.api.PreferenceApi
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.corepreference.impl.util.decode.decodeJsonOrNull
import com.yumaoem.corepreference.model.BookedTokenDetailsDTO
import com.yumaoem.corepreference.model.PrefSupportDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class YumaPrefUtilImpl(
    private val preferenceApi: PreferenceApi
): YumaPrefUtilApi {

    private var cachedBearerTokens: BearerTokens? = null

    private var cachedBookedTokenDetails: BookedTokenDetailsDTO? = null

    override suspend fun getUserId(): Flow<Int> {
        return preferenceApi.getInt(USER_ID)
    }

    override suspend fun saveUserId(userId: Int) {
        preferenceApi.putInt(USER_ID, userId)
    }

    override suspend fun saveBearerTokens(bearerTokens: BearerTokens) {
        val jsonString = Json.encodeToString(bearerTokens)
        preferenceApi.putString(BEARER_TOKENS, jsonString)
        cachedBearerTokens = bearerTokens // update in-memory cache
    }

    override suspend fun getBearerTokens(): BearerTokens? {
        return cachedBearerTokens ?: preferenceApi.getString(BEARER_TOKENS)
            .firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?.decodeJsonOrNull<BearerTokens>()
            ?.also { cachedBearerTokens = it }
    }

    override fun clearBearerTokens() {
        runBlocking {
            cachedBearerTokens = null
            preferenceApi.removeString(BEARER_TOKENS)
        }
    }

    override suspend fun saveUserData(user: User) {
        val jsonString = Json.encodeToString(user)
        preferenceApi.putString(USER_DATA, jsonString)
    }

    override suspend fun getFCMToken(): Flow<String> {
        return preferenceApi.getString(FCM_TOKEN)
    }

    override suspend fun saveFcmToken(token: String) {
        preferenceApi.putString(FCM_TOKEN, token)
    }

    override suspend fun getUserData(): User? {
        return preferenceApi.getString(USER_DATA)
            .firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?.decodeJsonOrNull<User>()
    }

    override suspend fun saveBookedTokenDetails(
        bookedTokenDetails: BookedTokenDetailsDTO
    ) {
        cachedBookedTokenDetails = bookedTokenDetails
    }

    override suspend fun getBookedTokenDetails(): BookedTokenDetailsDTO? {
        return cachedBookedTokenDetails
    }

    override suspend fun getAccessToken(): String? {
        val tokens = getBearerTokens()
        return tokens?.accessToken
    }

    override suspend fun isUserLoggedIn(): Boolean {
        val tokens = getBearerTokens()
        val user = getUserData()
        return tokens != null && user != null
    }

    override suspend fun removeBookedTokenDetails() {
        preferenceApi.removeString(BOOKED_TOKEN)
    }

    override fun logoutUser() {
        runBlocking {
            preferenceApi.clearAll()
        }
    }

    override suspend fun getLastCachedStationTimeStamp(): Long {
        return runBlocking {
            preferenceApi.getLong(KEY_LAST_UPDATE).firstOrNull()?:Long.MIN_VALUE
        }
    }

    override suspend fun saveLastCachedStationTimeStamp(timeStamp: Long) {
        preferenceApi.putLong(KEY_LAST_UPDATE, timeStamp)
    }

    override suspend fun saveSupportDetails(
        phoneNumber: Long,
        defaultMessage: String
    ) {
        val jsonString = Json.encodeToString(PrefSupportDetails(phoneNumber, defaultMessage))
        preferenceApi.putString(SUPPORT_DETAILS, jsonString)
    }

    override suspend fun getSupportDetails(): Flow<PrefSupportDetails?> {
        return preferenceApi.getString(SUPPORT_DETAILS)
            .map { json ->
                json.takeIf { it.isNotBlank() }
                    ?.decodeJsonOrNull<PrefSupportDetails>()
            }
    }


    override suspend fun setB2cStatus(isB2c: Boolean) {
        val existingUser = getUserData()
        if (existingUser != null) {
            val updatedUser = existingUser.copy(isPrePaidUser = isB2c)
            saveUserData(updatedUser)
        }
    }

    override suspend fun getB2cStatus(): Flow<Boolean> {
        return preferenceApi.getString(USER_DATA)
            .map { json ->
                json.takeIf { it.isNotBlank() }
                    ?.decodeJsonOrNull<User>()
                    ?.isPrePaidUser ?: false
            }
    }

}