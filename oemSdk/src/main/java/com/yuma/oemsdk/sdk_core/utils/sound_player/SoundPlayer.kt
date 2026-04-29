package com.yumaoem.core.utils.sound_player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.yumaoem.core.utils.context.AndroidContextProvider
import java.io.File
import java.io.FileOutputStream



class SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private val context: Context = AndroidContextProvider.context!!

    /**
     * Plays sound from a ByteArray once
     * @param audioData The audio data as ByteArray
     * @param format The audio format (e.g., "mp3", "wav", "m4a")
     * @param onComplete Optional callback when playback completes
     * @param onError Optional callback when an error occurs
     */
     fun playSound(
        audioData: ByteArray,
        format: String,
        onComplete: (() -> Unit)?,
        onError: ((String) -> Unit)?
    ) {
        try {
            release()
            val tempFile = File.createTempFile("audio_temp", ".$format", context.cacheDir)
            tempFile.deleteOnExit()
            FileOutputStream(tempFile).use { fos ->
                fos.write(audioData)
            }
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setDataSource(tempFile.absolutePath)

                setOnCompletionListener {
                    onComplete?.invoke()
                    release()
                    tempFile.delete()
                }

                setOnErrorListener { _, what, extra ->
                    onError?.invoke("MediaPlayer error: what=$what, extra=$extra")
                    release()
                    tempFile.delete()
                    true
                }

                prepare()
                start()
            }
        } catch (e: Exception) {
            onError?.invoke("Failed to play sound: ${e.message}")
            release()
        }
    }

     fun release() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            release()
        }
        mediaPlayer = null
    }
}