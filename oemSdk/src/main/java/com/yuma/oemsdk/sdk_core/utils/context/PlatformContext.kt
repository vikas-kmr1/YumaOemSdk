package com.yumaoem.core.utils.context

import android.content.Context

object PlatformContext {
    fun getApplicationContext(): Context? {
        return AndroidContextProvider.context
    }
}

object AndroidContextProvider {
    var context: Context? = null
}
