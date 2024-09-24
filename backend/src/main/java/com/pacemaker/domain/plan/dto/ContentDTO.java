package com.pacemaker.domain.plan.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ContentDTO(@NotNull String message, @NotNull Context context, @NotNull Plan plan) {

	@Builder
	public record Context(
		String goal, Integer goalTime, Integer goalDistance, List<String> trainDayOfWeek, UserInfo userInfo
	) {
		public record UserInfo(
			Integer age, Integer height, Integer weight, String gender, List<String> injuries, Integer recentRunPace,
			Integer recentRunDistance, Integer recentRunHeartRate
		) {
		}
	}

	@Builder
	public record Plan(
		Integer totalDays, Integer totalTimes, Integer totalDistances, List<PlanTrain> planTrains
	) {
		public record PlanTrain(
			Integer index, String trainDate, String paramType, Integer sessionTime, Integer sessionDistance,
			Integer repeat, Integer trainParam, Integer trainPace, Integer interParam
		) {
		}
	}
}
