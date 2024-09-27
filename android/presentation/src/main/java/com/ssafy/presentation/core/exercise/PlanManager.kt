package com.ssafy.presentation.core.exercise

import com.ssafy.domain.dto.User
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.domain.utils.DANNY
import com.ssafy.domain.utils.DANNY_FEAT
import com.ssafy.domain.utils.JAMIE
import com.ssafy.domain.utils.JAMIE_FEAT
import com.ssafy.domain.utils.MIKE
import com.ssafy.domain.utils.MIKE_FEAT
import com.ssafy.presentation.utils.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanManager @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val dataStoreRepository: DataStoreRepository,
    private val getPlanInfoUseCase: GetPlanInfoUseCase,
) {
    lateinit var user: User
    lateinit var coach: String
    lateinit var plan: PlanTrain

    fun syncPlanInfo(onFailure: ((exception: Throwable) -> Unit)? = null) {
        coroutineScope.launch {
            runCatching {
                user = dataStoreRepository.getUser()
                coach = when (user.coachNumber) {
                    MIKE -> MIKE_FEAT
                    JAMIE -> JAMIE_FEAT
                    DANNY -> DANNY_FEAT
                    else -> ""
                }
                plan = getPlanInfoUseCase().planTrains.firstOrNull {
                    it.trainDate.toLocalDate().atStartOfDay() == LocalDate.now().atStartOfDay()
                } ?: PlanTrain()
            }.onFailure {
                onFailure?.invoke(it)
            }
        }
    }
}