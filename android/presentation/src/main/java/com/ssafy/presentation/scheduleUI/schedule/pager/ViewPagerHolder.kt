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
        val trainInfoTitle = binding.tvInst
        if (item.planTrain != null) {
            trainInfoTitle.text = item.planTrain.toContentString()
            trainInfoTitle.isVisible = true
        }
        if (item.planTrain != null) {
            binding.lyResultInfo.isVisible = true
            val trainInfoView = binding.lyResultInfo
            trainInfoView.makeEntryChart(item.planTrain)
        } else {
            trainInfoTitle.text = "자율 훈련"
            binding.lyResultInfo.isVisible = false
        }

        if (item.trainReport != null) {
            item.trainReport?.apply {
                makeResult(trainEvaluation, trainResult)
                binding.map.isVisible = true
                binding.hv.isVisible = true
                makeMap(trainResult.trainMap)
                binding.hv.setData(trainResult.heartZone)
                if (trainResult.coachNumber != null && trainResult.coachMessage != null) {
                    binding.lyTrainResultCoach.isVisible = true
                    binding.lyTrainResultCoach.isVisible = true
                    makeCoach(trainResult.coachNumber, trainResult.coachMessage)
                } else {
                    binding.lyTrainResultCoach.isVisible = false
                }
            }
        } else {
            binding.lyTrainResult.isVisible = false
            binding.map.isVisible = false
            binding.lyTrainResultCoach.isVisible = false
            binding.hv.isVisible = false
        }
    }

    private fun makeMap(locationList: List<List<Double>>) {
        val map = binding.map
        map.setMapView(locationList)

    }

    private fun makeCoach(coachNumber: Long?, messages: List<String>?) {
        val coachList = binding.lyTrainResultCoach
        if (messages != null) {
            coachList.setList(messages)
        }
        if (coachNumber != null) {
            coachList.setCoachImage(coachNumber)
        }
    }

    private fun makeResult(score: TrainEvaluation?, result: TrainResult) {
        val pacePercent = score?.paceEvaluation
        val heartPercent = score?.heartRateEvaluation
        val stepPercent = score?.cadenceEvaluation

        val trainResultView = binding.lyTrainResult
        binding.lyTrainResult.isVisible = true
        trainResultView.setResultData(
            result.trainDistance,
            result.trainTime,
            result.heartRate,
            result.cadence,
            result.pace,
            result.kcal
        )
        if (pacePercent != null && heartPercent != null && stepPercent != null) {
            trainResultView.setPieChart(
                pacePercent.toFloat(),
                heartPercent.toFloat(),
                stepPercent.toFloat()
            )
        } else {
            trainResultView.unvisibleChart()
        }
    }
}