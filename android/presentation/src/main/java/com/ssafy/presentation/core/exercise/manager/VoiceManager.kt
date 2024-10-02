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
class VoiceManager @Inject constructor(coroutineScope: CoroutineScope) {
    private val voiceChannel = Channel<String>(Channel.UNLIMITED)
    private val mediaPlayer = MediaPlayer()

    init {
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

        with(mediaPlayer) {
            reset()
            setDataSource(file.path)
            prepare()
            start()

            while (mediaPlayer.isPlaying) delay(100)
            reset()
            file.delete()
        }
    }

    fun release() = runCatching {
        voiceChannel.close()
        mediaPlayer.release()
    }.onFailure { it.printStackTrace() }

}
