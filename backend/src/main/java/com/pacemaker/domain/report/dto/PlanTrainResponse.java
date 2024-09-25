package com.pacemaker.domain.report.dto;

import com.pacemaker.domain.plan.entity.PlanTrain;

import lombok.Builder;

@Builder
public record PlanTrainResponse(Integer index, String trainDate, String paramType, Integer sessionTime,
								Integer sessionDistance, Integer repetition, Integer trainParam, Integer trainPace,
								Integer interParam) {

	public static PlanTrainResponse of(Integer index, PlanTrain planTrain) {
		return PlanTrainResponse.builder()
			.index(index)
			.trainDate(planTrain.getTrainDate().toString())
			.paramType(planTrain.getParamType())
			.sessionTime(planTrain.getSessionTime())
			.sessionDistance(planTrain.getSessionDistance())
			.repetition(planTrain.getRepetition())
			.trainParam(planTrain.getTrainParam())
			.trainPace(planTrain.getTrainPace())
			.interParam(planTrain.getInterParam())
			.build();
	}
}
