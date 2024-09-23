package com.pacemaker.domain.openai.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ChatDTO(@NotNull String message, @NotNull Context context, @NotNull Plan plan) {

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
			Integer index, Date trainDate, String trainType, Integer sessionTime, Integer sessionDistance,
			Integer repeat, Integer trainParam, Integer trainPace, Integer interParam
		) {
		}
	}
}
