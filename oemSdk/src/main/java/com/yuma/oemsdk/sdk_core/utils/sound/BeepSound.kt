package com.yumaoem.core.utils.sound

import android.media.AudioManager
import android.media.ToneGenerator

private val toneGenerator by lazy {
    ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
}

fun playBeep() {
    try {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
    } catch (_: Exception) { }
}
