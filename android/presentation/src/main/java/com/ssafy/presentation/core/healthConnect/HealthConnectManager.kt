package com.ssafy.presentation.core.healthConnect

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.InsertRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ssafy.presentation.core.exercise.data.ExerciseData
import com.ssafy.presentation.core.exercise.data.cadence
import com.ssafy.presentation.core.exercise.data.endTime
import com.ssafy.presentation.core.exercise.data.heartRate
import com.ssafy.presentation.core.exercise.data.location
import com.ssafy.presentation.core.exercise.data.speed
import com.ssafy.presentation.core.exercise.data.startTime
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.reflect.KClass

private const val TAG = "HealthConnectManager_PACEMAKER"

class HealthConnectManager @Inject constructor(@ApplicationContext val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val healthConnectCompatibleApps by lazy {
        val intent = Intent("androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE")

        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            )
        }

        packages.associate {
            val icon = try {
                context.packageManager.getApplicationIcon(it.activityInfo.packageName)
            } catch (e: NotFoundException) {
                null
            }
            val label = context.packageManager.getApplicationLabel(it.activityInfo.applicationInfo)
                .toString()
            it.activityInfo.packageName to
                    HealthConnectAppInfo(
                        packageName = it.activityInfo.packageName,
                        icon = icon,
                        appLabel = label
                    )
        }
    }

    var availability = MutableStateFlow(SDK_UNAVAILABLE)
        private set

    fun checkAvailability() {
        availability.value = HealthConnectClient.getSdkStatus(context)
    }

    init {
        checkAvailability()
    }

    private val permissions = setOf(
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(SpeedRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(StepsCadenceRecord::class),
        HealthPermission.getReadPermission(StepsCadenceRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(HeightRecord::class),
        HealthPermission.getReadPermission(HeightRecord::class),
        HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE,
    )

    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    fun launchPermissionsLauncher(launcher: ActivityResultLauncher<Set<String>>) {
        launcher.launch(permissions)
    }

    suspend fun revokeAllPermissions() {
        healthConnectClient.permissionController.revokeAllPermissions()
    }

    suspend fun readExerciseSessions(start: Instant, end: Instant): List<ExerciseSessionRecord> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    suspend fun writeExerciseSession(
        title: String,
        exerciseData: ExerciseData,
    ): InsertRecordsResponse = with(exerciseData) {
        return healthConnectClient.insertRecords(
            listOf(
                ExerciseSessionRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                    title = title,
                    exerciseRoute = ExerciseRoute(location)
                ),
                StepsRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    count = totalSteps
                ),
                DistanceRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    distance = totalDistance
                ),
                TotalCaloriesBurnedRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    energy = totalEnergyBurned
                ),
                HeartRateRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    samples = heartRate
                ),
                StepsCadenceRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    samples = cadence,
                ),
                SpeedRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    samples = speed
                ),
            )
        )
    }

    suspend fun deleteExerciseSession(uid: String) {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        healthConnectClient.deleteRecords(
            ExerciseSessionRecord::class,
            recordIdsList = listOf(uid),
            clientRecordIdsList = emptyList()
        )
        val timeRangeFilter = TimeRangeFilter.between(
            exerciseSession.record.startTime,
            exerciseSession.record.endTime
        )
        val rawDataTypes: Set<KClass<out Record>> = setOf(
            HeartRateRecord::class,
            SpeedRecord::class,
            DistanceRecord::class,
            StepsRecord::class,
            TotalCaloriesBurnedRecord::class
        )
        rawDataTypes.forEach { rawType ->
            healthConnectClient.deleteRecords(rawType, timeRangeFilter)
        }
    }

    suspend fun readAssociatedSessionData(
        uid: String
    ): ExerciseSessionDetail {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        val timeRangeFilter = TimeRangeFilter.between(
            startTime = exerciseSession.record.startTime,
            endTime = exerciseSession.record.endTime
        )
        val aggregateDataTypes = setOf(
            ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
            StepsRecord.COUNT_TOTAL,
            SpeedRecord.SPEED_AVG,
            DistanceRecord.DISTANCE_TOTAL,
            TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            HeartRateRecord.BPM_AVG,
            StepsCadenceRecord.RATE_AVG,
        )
        val dataOriginFilter = setOf(exerciseSession.record.metadata.dataOrigin)
        val aggregateRequest = AggregateRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = timeRangeFilter,
            dataOriginFilter = dataOriginFilter
        )
        val aggregateData = healthConnectClient.aggregate(aggregateRequest)

        return ExerciseSessionDetail(
            uid = uid,
            totalActiveTime = aggregateData[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL],
            totalSteps = aggregateData[StepsRecord.COUNT_TOTAL],
            totalDistance = aggregateData[DistanceRecord.DISTANCE_TOTAL],
            totalEnergyBurned = aggregateData[TotalCaloriesBurnedRecord.ENERGY_TOTAL],
            avgHeartRate = aggregateData[HeartRateRecord.BPM_AVG],
            avgCadence = aggregateData[StepsCadenceRecord.RATE_AVG],
        )
    }

    suspend fun readBucketSessionData(
        uid: String
    ): List<ExerciseSessionDetail> {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        val timeRangeFilter = TimeRangeFilter.between(
            startTime = exerciseSession.record.startTime,
            endTime = exerciseSession.record.endTime
        )
        val aggregateDataTypes = setOf(
            ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
            StepsRecord.COUNT_TOTAL,
            SpeedRecord.SPEED_AVG,
            DistanceRecord.DISTANCE_TOTAL,
            TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            HeartRateRecord.BPM_AVG,
            StepsCadenceRecord.RATE_AVG,
        )
        val dataOriginFilter = setOf(exerciseSession.record.metadata.dataOrigin)
        val aggregateRequest = AggregateGroupByDurationRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = timeRangeFilter,
            timeRangeSlicer = Duration.ofSeconds(1L),
            dataOriginFilter = dataOriginFilter
        )

        val bucketData = healthConnectClient.aggregateGroupByDuration(aggregateRequest)

        return bucketData.map {
            val aggregateData = it.result
            ExerciseSessionDetail(
                uid = uid,
                totalActiveTime = aggregateData[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL],
                totalSteps = aggregateData[StepsRecord.COUNT_TOTAL],
                totalDistance = aggregateData[DistanceRecord.DISTANCE_TOTAL],
                totalEnergyBurned = aggregateData[TotalCaloriesBurnedRecord.ENERGY_TOTAL],
                avgHeartRate = aggregateData[HeartRateRecord.BPM_AVG],
                avgCadence = aggregateData[StepsCadenceRecord.RATE_AVG],
            )
        }
    }

    suspend fun readExerciseRoute(uid: String): ExerciseSessionRecord {
        return healthConnectClient.readRecord(ExerciseSessionRecord::class, uid).record
    }

    suspend fun writeWeightInput(weight: WeightRecord) {
        val records = listOf(weight)
        healthConnectClient.insertRecords(records)
    }

    suspend fun readWeightInputs(start: Instant, end: Instant): List<WeightRecord> {
        val request = ReadRecordsRequest(
            recordType = WeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    suspend fun writeHeightInput(height: HeightRecord) {
        val records = listOf(height)
        healthConnectClient.insertRecords(records)
    }

    suspend fun readHeightInputs(start: Instant, end: Instant): List<HeightRecord> {
        val request = ReadRecordsRequest(
            recordType = HeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }
}
