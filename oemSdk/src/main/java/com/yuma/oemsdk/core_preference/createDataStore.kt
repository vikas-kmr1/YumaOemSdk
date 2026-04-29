package com.yumaoem.corepreference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "settings.preferences_pb"

object AppSettings {
    private lateinit var dataStore: DataStore<Preferences>
    @OptIn(InternalCoroutinesApi::class)
    private val lock = SynchronizedObject()

    @OptIn(InternalCoroutinesApi::class)
    fun getDataStore(producePath: () -> String): DataStore<Preferences> {
        return synchronized(lock) {
            if (AppSettings::dataStore.isInitialized) {
                dataStore
            } else {
                PreferenceDataStoreFactory.createWithPath(
                    produceFile = { producePath().toPath() }
                ).also { dataStore = it }
            }
        }
    }
}

fun createDataStore(context: Any?): DataStore<Preferences> {
    require(
        value = context is Context,
        lazyMessage = { "Context object is required." }
    )
    return AppSettings.getDataStore(
        producePath = {
            context.filesDir
                .resolve(dataStoreFileName)
                .absolutePath
        }
    )
}


