package com.yumaoem.corepreference.api

import com.yumaoem.core.model.auth.BearerTokens
import com.yumaoem.core.model.auth.User
import com.yumaoem.corepreference.model.BookedTokenDetailsDTO
import com.yumaoem.corepreference.model.PrefSupportDetails
import kotlinx.coroutines.flow.Flow

interface YumaPrefUtilApi {
    suspend fun getUserId(): Flow<Int>

    suspend fun saveUserId(userId:Int)

    suspend fun saveBearerTokens(bearerTokens: BearerTokens)

    suspend fun getBearerTokens(): BearerTokens?

    suspend fun saveOrderId(orderId: Int)

    suspend fun getOrderId():  Int?

    suspend fun getUserData(): User?

    suspend fun saveBookedTokenDetails(bookedTokenDetails: BookedTokenDetailsDTO)

    suspend fun getBookedTokenDetails(): BookedTokenDetailsDTO?

    suspend fun getAccessToken(): String?

    fun clearBearerTokens()

    suspend fun saveUserData(user: User)

    suspend fun getFCMToken(): Flow<String?>

    suspend fun saveFcmToken(token: String)

    suspend fun isUserLoggedIn(): Boolean

    suspend fun removeBookedTokenDetails()

    fun logoutUser()

    suspend fun getLastCachedStationTimeStamp(): Long

    suspend fun saveLastCachedStationTimeStamp(timeStamp: Long)

    suspend fun saveSupportDetails(phoneNumber: Long, defaultMessage: String)

    suspend fun getSupportDetails(): Flow<PrefSupportDetails?>

    suspend fun setB2cStatus(isB2c: Boolean)

    suspend fun getB2cStatus():  Flow<Boolean>
}