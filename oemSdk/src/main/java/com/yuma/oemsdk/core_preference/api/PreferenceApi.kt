package com.yumaoem.corepreference.api

import kotlinx.coroutines.flow.Flow

/**
 * Multiplatform Preference API
 * */
interface PreferenceApi {

    // *** String Functions *** //

    /**
     * Function to get [String] value from preferences
     * Default value will be null if key is not found
     * */
    suspend fun getString(key: String): Flow<String>

    /**
     * Function to get [String] value from preferences
     * Default value will be [default] key
     * */
    suspend fun getString(key: String, default: String): Flow<String>

    /**
     * Function to put [String] value in preferences
     * */
    suspend fun putString(key: String, value: String)

    // *** Int Functions *** //

    /**
     * Function to get [Int] value from preferences
     * Default value will be null if key is not found
     * */
    suspend fun getInt(key: String): Flow<Int>

    /**
     * Function to get [Int] value from preferences
     * Default value will be [default] key
     * */
    suspend fun getInt(key: String, default: Int): Flow<Int>

    /**
     * Function to put [Int] value in preferences
     * */
    suspend fun putInt(key: String, value: Int)


    // *** Long Functions *** //
    /**
     * Function to get [Long] value from preferences
     * Default value will be null if key is not found
     * */
    suspend fun getLong(key: String): Flow<Long>

    /**
     * Function to get [Long] value from preferences
     * Default value will be [default] key
     * */
    suspend fun getLong(key: String, default: Long): Flow<Long>

    /**
     * Function to put [Long] value in preferences
     * */
    suspend fun putLong(key: String, value: Long)

    // *** Float Functions *** //

    /**
     * Function to get [Float] value from preferences
     * Default value will be null if key is not found
     * */
    suspend fun getFloat(key: String): Flow<Float>

    /**
     * Function to get [Float] value from preferences
     * Default value will be [default] key
     * */
    suspend fun getFloat(key: String, default: Float): Flow<Float>

    /**
     * Function to put [Float] value in preferences
     * */
    suspend fun putFloat(key: String, value: Float)

    // *** Double Functions *** //

    /**
     * Function to get [Double] value from preferences
     * Default value will be null if key is not found
     * */
    suspend fun getDouble(key: String): Flow<Double>

    /**
     * Function to get [Double] value from preferences
     * Default value will be [default] key
     * */
    suspend fun getDouble(key: String, default: Double): Flow<Double>

    /**
     * Function to put [Double] value in preferences
     * */
    suspend fun putDouble(key: String, value: Double)


    // *** Boolean Function *** //

    /**
     * Function to get [Boolean] value from preferences
     * Default value will be null if key is not found
     * */
    suspend fun getBoolean(key: String): Flow<Boolean>

    /**
     * Function to get [Boolean] value from preferences
     * Default value will be [default] key
     * */
    suspend fun getBoolean(key: String, default: Boolean): Flow<Boolean>

    /**
     * Function to put [Boolean] value in preferences
     * */
    suspend fun putBoolean(key: String, value: Boolean)

    /**
     * Function to remove the String key & values in the preferences
     * */
    suspend fun removeString(key: String)

    /**
     *  Function to remove the Boolean key & values in the preferences
     */
    suspend fun removeBoolean(key: String)

    /**
     *  Function to remove the Int key & values in the preferences
     */
    suspend fun removeInt(key: String)

    /**
     *  Function to remove the Long key & values in the preferences
     */
    suspend fun removeLong(key: String)

    /**
     *  Function to remove the Float key & values in the preferences
     */
    suspend fun removeFloat(key: String)

    /**
     *  Function to remove the Double key & values in the preferences
     */
    suspend fun removeDouble(key: String)

    /**
     * Function to clear all the values in the preferences
     * */
    suspend fun clearAll()
}