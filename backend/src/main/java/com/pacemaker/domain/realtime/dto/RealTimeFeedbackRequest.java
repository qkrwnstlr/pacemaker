package com.pacemaker.domain.realtime.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RealTimeFeedbackRequest(@NotNull Long coachNumber, @NotNull String coachTone, @NotNull List<Integer> meanHeartRate,
									  @NotNull List<Integer> meanPace, @NotNull List<Integer> meanCadence, @NotNull float nowDistance,
									  @NotNull PlanTrain planTrain) {

	@Builder
	public record PlanTrain(
		String trainDate, String paramType, Integer sessionTime, Integer sessionDistance,
		Integer repetition, Integer trainParam, Integer trainPace, Integer interParam
	) {
	}
}
