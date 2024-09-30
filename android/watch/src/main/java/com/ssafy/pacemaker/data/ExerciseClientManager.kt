package com.ssafy.pacemaker.data

import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.ComparisonType
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeCondition
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseGoal
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseTrackedStatus.Companion.NO_EXERCISE_IN_PROGRESS
import androidx.health.services.client.data.ExerciseTrackedStatus.Companion.OTHER_APP_IN_PROGRESS
import androidx.health.services.client.data.ExerciseTrackedStatus.Companion.OWNED_EXERCISE_IN_PROGRESS
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseTypeCapabilities
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationAvailability
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.markLap
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class ExerciseClientManager @Inject constructor(healthServicesClient: HealthServicesClient) {
    private val exerciseClient: ExerciseClient = healthServicesClient.exerciseClient

    suspend fun getExerciseCapabilities(): ExerciseTypeCapabilities? {
        val capabilities = exerciseClient.getCapabilities()

        return if (ExerciseType.RUNNING in capabilities.supportedExerciseTypes) {
            capabilities.getExerciseTypeCapabilities(ExerciseType.RUNNING)
        } else {
            null
        }
    }

    private var thresholds = Thresholds(0.0, Duration.ZERO)

    fun updateGoals(newThresholds: Thresholds) {
        thresholds = newThresholds.copy()
    }

    suspend fun isExerciseInProgress() = exerciseClient.isExerciseInProgress()

    suspend fun isTrackingExerciseInAnotherApp() = exerciseClient.isTrackingExerciseInAnotherApp()

    suspend fun startExercise() {
        val capabilities = getExerciseCapabilities() ?: return

        val exerciseInfo = exerciseClient.getCurrentExerciseInfoAsync().await()
        when (exerciseInfo.exerciseTrackedStatus) {
            OTHER_APP_IN_PROGRESS -> {}
            OWNED_EXERCISE_IN_PROGRESS -> {}
            NO_EXERCISE_IN_PROGRESS -> {
                val dataTypes = setOf(
                    DataType.HEART_RATE_BPM,
                    DataType.HEART_RATE_BPM_STATS,
                    DataType.CALORIES_TOTAL,
                    DataType.DISTANCE_TOTAL,
                    DataType.PACE,
                    DataType.PACE_STATS,
                    DataType.RUNNING_STEPS_TOTAL,
                    DataType.STEPS_PER_MINUTE,
                    DataType.STEPS_PER_MINUTE_STATS,
                    DataType.VO2_MAX,
                    DataType.VO2_MAX_STATS,
                    DataType.LOCATION,
                ).intersect(capabilities.supportedDataTypes)
                val exerciseGoals = mutableListOf<ExerciseGoal<*>>()
                if (supportsCalorieGoal(capabilities)) {
                    // Create a one-time goal.
                    exerciseGoals.add(
                        ExerciseGoal.createOneTimeGoal(
                            DataTypeCondition(
                                dataType = DataType.CALORIES_TOTAL,
                                threshold = CALORIES_THRESHOLD,
                                comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL
                            )
                        )
                    )
                }

                // Set a distance goal if it's supported by the exercise and the user has entered one
                if (supportsDistanceMilestone(capabilities) && thresholds.distanceIsSet) {
                    exerciseGoals.add(
                        ExerciseGoal.createOneTimeGoal(
                            condition = DataTypeCondition(
                                dataType = DataType.DISTANCE_TOTAL,
                                threshold = thresholds.distance * 1000, //our app uses kilometers
                                comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL
                            )
                        )
                    )
                }

                // Set a duration goal if it's supported by the exercise and the user has entered one
                if (supportsDurationMilestone(capabilities) && thresholds.durationIsSet) {
                    exerciseGoals.add(
                        ExerciseGoal.createOneTimeGoal(
                            DataTypeCondition(
                                dataType = DataType.ACTIVE_EXERCISE_DURATION_TOTAL,
                                threshold = thresholds.duration.inWholeSeconds,
                                comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL
                            )
                        )
                    )
                }

                val supportsAutoPauseAndResume = capabilities.supportsAutoPauseAndResume

                val config = ExerciseConfig(
                    exerciseType = ExerciseType.RUNNING,
                    dataTypes = dataTypes,
                    isAutoPauseAndResumeEnabled = supportsAutoPauseAndResume,
                    isGpsEnabled = true,
                    exerciseGoals = exerciseGoals
                )

                exerciseClient.startExercise(config)
            }
        }
    }

    suspend fun prepareExercise() {
        val exerciseInfo = exerciseClient.getCurrentExerciseInfoAsync().await()
        when (exerciseInfo.exerciseTrackedStatus) {
            OTHER_APP_IN_PROGRESS -> {}
            OWNED_EXERCISE_IN_PROGRESS -> {}
            NO_EXERCISE_IN_PROGRESS -> {
                val warmUpConfig = WarmUpConfig(
                    exerciseType = ExerciseType.RUNNING,
                    dataTypes = setOf(DataType.HEART_RATE_BPM, DataType.LOCATION)
                )
                exerciseClient.prepareExercise(warmUpConfig)
            }
        }
    }

    suspend fun endExercise() {
        val exerciseInfo = exerciseClient.getCurrentExerciseInfoAsync().await()
        when (exerciseInfo.exerciseTrackedStatus) {
            OTHER_APP_IN_PROGRESS -> {}
            OWNED_EXERCISE_IN_PROGRESS -> {
                exerciseClient.endExercise()
            }

            NO_EXERCISE_IN_PROGRESS -> {}
        }
    }

    suspend fun pauseExercise() {
        val exerciseInfo = exerciseClient.getCurrentExerciseInfoAsync().await()
        when (exerciseInfo.exerciseTrackedStatus) {
            OTHER_APP_IN_PROGRESS -> {}
            OWNED_EXERCISE_IN_PROGRESS -> {
                exerciseClient.pauseExercise()
            }

            NO_EXERCISE_IN_PROGRESS -> {}
        }
    }

    suspend fun resumeExercise() {
        val exerciseInfo = exerciseClient.getCurrentExerciseInfoAsync().await()
        when (exerciseInfo.exerciseTrackedStatus) {
            OTHER_APP_IN_PROGRESS -> {}
            OWNED_EXERCISE_IN_PROGRESS -> {
                exerciseClient.resumeExercise()
            }

            NO_EXERCISE_IN_PROGRESS -> {}
        }
    }

    suspend fun markLap() {
        if (exerciseClient.isExerciseInProgress()) {
            exerciseClient.markLap()
        }
    }

    val exerciseUpdateFlow = callbackFlow {
        val callback = object : ExerciseUpdateCallback {
            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                trySendBlocking(ExerciseMessage.ExerciseUpdateMessage(update))
            }

            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
                trySendBlocking(ExerciseMessage.LapSummaryMessage(lapSummary))
            }

            override fun onRegistered() {
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onAvailabilityChanged(
                dataType: DataType<*, *>, availability: Availability
            ) {
                if (availability is LocationAvailability) {
                    trySendBlocking(ExerciseMessage.LocationAvailabilityMessage(availability))
                }
            }
        }

        exerciseClient.setUpdateCallback(callback)
        awaitClose {
            // Ignore async result
            exerciseClient.clearUpdateCallbackAsync(callback)
        }
    }

    private companion object {
        const val CALORIES_THRESHOLD = 250.0
    }
}

data class Thresholds(
    var distance: Double,
    var duration: Duration,
    var durationIsSet: Boolean = duration != Duration.ZERO,
    var distanceIsSet: Boolean = distance != 0.0,
)


sealed class ExerciseMessage {
    class ExerciseUpdateMessage(val exerciseUpdate: ExerciseUpdate) : ExerciseMessage()
    class LapSummaryMessage(val lapSummary: ExerciseLapSummary) : ExerciseMessage()
    class LocationAvailabilityMessage(val locationAvailability: LocationAvailability) :
        ExerciseMessage()
}



