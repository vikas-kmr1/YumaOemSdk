package com.yumaoem.corepreference.impl.util

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

/**
 * Extension function to create a String Preferences Key
 */
fun String.toStringPreferencesKey(): Preferences.Key<String> {
    return stringPreferencesKey(this)
}

/**
 *  Extension function to create an Int Preferences Key
 */
fun String.toIntPreferencesKey(): Preferences.Key<Int> {
    return intPreferencesKey(this)
}

/**
 * Extension function to create an Long Preferences Key
 */
fun String.toLongPreferencesKey(): Preferences.Key<Long> {
    return longPreferencesKey(this)
}

/**
 * Extension function to create an Double Preferences Key
 */
fun String.toFloatPreferencesKey(): Preferences.Key<Float> {
    return floatPreferencesKey(this)
}

/**
 * Extension function to create an Double Preferences Key
 */
fun String.toDoublePreferencesKey(): Preferences.Key<Double> {
    return doublePreferencesKey(this)
}


/**
 * Extension function to create an Boolean Preferences Key
 */
fun String.toBooleanPreferencesKey(): Preferences.Key<Boolean> {
    return booleanPreferencesKey(this)
}



