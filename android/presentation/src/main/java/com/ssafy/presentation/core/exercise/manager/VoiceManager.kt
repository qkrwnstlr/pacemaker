package com.ssafy.presentation.core.exercise.manager

import android.media.MediaPlayer
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@ServiceScoped
class VoiceManager @Inject constructor(private val coroutineScope: CoroutineScope) {
    private lateinit var voiceChannel: Channel<String>
    private var mediaPlayer: MediaPlayer? = null

    fun connect() {
        if (mediaPlayer != null) return
        mediaPlayer = MediaPlayer()
        voiceChannel = Channel(Channel.UNLIMITED)
        coroutineScope.launch(Dispatchers.Main) {
            for (path in voiceChannel) {
                playVoice(path)
            }
        }
    }

    fun addVoicePath(path: String) = voiceChannel.trySend(path)

    private suspend fun playVoice(path: String) {
        val file = File(path)
        if (!file.exists()) return

        mediaPlayer?.run {
            reset()
            setDataSource(file.path)
            prepare()
            start()
            while (mediaPlayer?.isPlaying == true) delay(100)
            mediaPlayer?.reset()
        }
        file.delete()
    }

    fun disconnect() = runCatching {
        voiceChannel.close()
        mediaPlayer?.release()
        mediaPlayer = null
    }.onFailure { it.printStackTrace() }
}
