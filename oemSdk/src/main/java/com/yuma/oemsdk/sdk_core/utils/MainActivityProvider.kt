package com.yuma.oemsdk.sdk_core.utils

interface MainActivityProvider {
    fun getMainActivityClass(): Class<*>
}