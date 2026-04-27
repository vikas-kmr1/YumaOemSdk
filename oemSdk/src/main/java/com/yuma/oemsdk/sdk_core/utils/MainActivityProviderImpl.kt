package com.yuma.oemsdk.sdk_core.utils

import com.yuma.oemsdk.SdkMainActivity

object ComposeAppMainActivityProvider : MainActivityProvider {
    override fun getMainActivityClass(): Class<*> = SdkMainActivity::class.java
}