package com.yumaoem.corepreference.impl.util.decode

import kotlinx.serialization.json.Json

inline fun <reified T> String.decodeJsonOrNull(): T? {
    return takeIf { it.isNotBlank() }?.let {
        try {
            Json.decodeFromString<T>(it)
        } catch (e: Exception) {
            null
        }
    }
}
