package com.yumaoem.core.utils.resource_reader

import com.yumaoem.core.utils.context.AndroidContextProvider

class FileReader constructor() {
    private val context = AndroidContextProvider.context

    fun readBytes(fileName: String): ByteArray? {

        if (context == null) {
            return null
        }

        val resourceName = fileName.substringBeforeLast(".")
        val resourceType = "raw"
        val packageName = context.packageName

        val resourceId = context.resources.getIdentifier(resourceName, resourceType, packageName)

        if (resourceId == 0) {
            return null
        }

        return try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                val bytes = inputStream.readBytes()
                bytes
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}