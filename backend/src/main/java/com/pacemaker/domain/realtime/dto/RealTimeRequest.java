package com.pacemaker.domain.realtime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RealTimeRequest(@NotNull String coachTone, @NotNull Integer meanHeartRate,
							  @NotNull Integer meanPace, @NotNull Integer meanCadence, @NotNull float nowDistance,
							  @NotNull PlanTrain planTrain) {

	@Builder
	public record PlanTrain(
		String trainDate, String paramType, Integer sessionTime, Integer sessionDistance,
		Integer repetition, Integer trainParam, Integer trainPace, Integer interParam
	) {
	}
}
