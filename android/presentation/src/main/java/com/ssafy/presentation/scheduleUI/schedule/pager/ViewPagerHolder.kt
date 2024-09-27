package com.ssafy.presentation.scheduleUI.schedule.pager

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.reports.TrainEvaluation
import com.ssafy.domain.dto.reports.TrainResult
import com.ssafy.presentation.databinding.PlanItemBinding
import com.ssafy.presentation.utils.toContentString

class ViewPagerHolder(private val binding: PlanItemBinding) : ViewHolder(binding.root) {

    fun bind(item: Report) {
        if (item.planTrain != null) {
            val trainInfoTitle = binding.tvInst
            trainInfoTitle.setTitle(
                item.planTrain?.trainDate ?: "",
                item.planTrain.toContentString(),
                item.trainReport == null
            )
        }
        if (item.planTrain != null) {
            binding.lyResultInfo.isVisible = true
            val trainInfoView = binding.lyResultInfo
            trainInfoView.makeEntryChart(item.planTrain)
        } else {
            binding.lyResultInfo.isVisible = false
        }

        if (item.trainReport != null) {
            binding.lyTrainResult.isVisible = true
            binding.map.isVisible = true
            binding.lyTrainResultCoach.isVisible = true

            item.trainReport?.apply {
                makeResult(trainEvaluation, trainResult)
                makeMap(trainResult.trainMap)
                makeCoach()
            }
        } else {
            binding.lyTrainResult.isVisible = false
            binding.map.isVisible = false
            binding.lyTrainResultCoach.isVisible = false
        }
    }

    private fun makeMap(locationList: List<List<Double>>) {
        val map = binding.map
        map.setMapView(locationList)

    }

    private fun makeCoach() {

    }

    private fun makeResult(score: TrainEvaluation, result: TrainResult) {
        val pacePercent = score.paceEvaluation
        val heartPercent = score.heartRateEvaluation
        val stepPercent = score.cadenceEvaluation

        val trainResultView = binding.lyTrainResult
        trainResultView.setResultData(
            result.trainDistance,
            result.trainTime,
            result.heartRate,
            result.cadence,
            result.pace,
            result.kcal
        )
        trainResultView.setPieChart(
            pacePercent.toFloat(),
            heartPercent.toFloat(),
            stepPercent.toFloat()
        )
    }


}