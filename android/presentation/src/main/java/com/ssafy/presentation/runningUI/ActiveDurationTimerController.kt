package com.ssafy.presentation.runningUI

import android.widget.TextView
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate.ActiveDurationCheckpoint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.presentation.utils.formatElapsedTime
import java.time.Duration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

class ActiveDurationTimerController(
    private val viewLifecycleOwner: LifecycleOwner,
    private val textViews: List<TextView>,
) {
    private var state = ExerciseState.PREPARING
    private var checkpoint = ActiveDurationCheckpoint(Instant.now(), Duration.ZERO)

    private var tickJob: Job? = null

    fun updateExerciseState(state: ExerciseState?, checkpoint: ActiveDurationCheckpoint?) {
        this.state = state ?: this.state
        this.checkpoint = checkpoint ?: this.checkpoint

        if (state == ExerciseState.ACTIVE) {
            startJob()
        } else {
            stopJob()
        }
    }

    private fun startJob() {
        if (tickJob == null) {
            tickJob = viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    while (true) {
                        delay(200)
                        updateText()
                    }
                }
            }
        }
    }

    private fun updateText() {
        val duration = checkpoint.activeDuration.plusMillis(
            System.currentTimeMillis() - checkpoint.time.toEpochMilli()
        )
        textViews.forEach { it.text = formatElapsedTime(duration, true) }
    }

    private fun stopJob() {
        tickJob?.cancel()
        tickJob = null
    }
}