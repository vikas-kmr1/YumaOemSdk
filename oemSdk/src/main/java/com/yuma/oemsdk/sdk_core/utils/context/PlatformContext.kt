package com.yumaoem.core.utils.context

import android.content.Context

object PlatformContext {
    fun getApplicationContext(): Any? {
        return AndroidContextProvider.context
    }
}

object AndroidContextProvider {
    var context: Context? = null
}
