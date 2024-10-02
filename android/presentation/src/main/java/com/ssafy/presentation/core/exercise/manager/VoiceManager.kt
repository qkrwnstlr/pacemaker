package com.ssafy.presentation.core.exercise.manager

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceManager @Inject constructor(private val coroutineScope: CoroutineScope) {
    private val voiceChannel = Channel<String>(Channel.UNLIMITED)
    private val mediaPlayer = MediaPlayer()
    private val mutex = Mutex()

    init {
        coroutineScope.launch(Dispatchers.Main) {
            for (path in voiceChannel) {
                mutex.withLock {
                    mutex.unlock()
                    playVoice(path)
                }
            }
        }
    }

    suspend fun addVoicePath(path: String) {
        voiceChannel.send(path)
    }

    private fun playVoice(path: String) {
        val file = File(path)
        if (!file.exists()) return

        mediaPlayer.apply {
            reset()
            setDataSource(file.path)
            prepare()
            start()
            setOnCompletionListener {
                reset()
                file.delete()
                coroutineScope.launch { mutex.lock() }
            }
        }
    }

    fun release() {
        voiceChannel.close()
        mediaPlayer.release()
    }
}