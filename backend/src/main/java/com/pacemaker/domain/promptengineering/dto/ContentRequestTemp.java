package com.pacemaker.domain.promptengineering.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContentRequestTemp(
	@NotNull @Schema(description = "채팅 메세지", example = "채팅 메세지 내용") String message,
	@NotNull Context context,
	@NotNull Plan plan,
	@NotNull String coachTone
) {

	@Builder
	public record Context(
		@Schema(example = "") String goal,
		@Schema(example = "0") Integer goalTime,
		@Schema(example = "0") Integer goalDistance,
		@Schema(example = "[]") List<String> trainDayOfWeek,
		UserInfo userInfo
	) {
		public record UserInfo(
			@Schema(example = "0") Integer age,
			@Schema(example = "0") Integer height,
			@Schema(example = "0") Integer weight,
			@Schema(example = "") String gender,
			@Schema(example = "[]") List<String> injuries,
			@Schema(example = "0") Integer recentRunPace,
			@Schema(example = "0") Integer recentRunDistance,
			@Schema(example = "0") Integer recentRunHeartRate
		) {
		}
	}

	public record Plan(
		@Schema(example = "0") Integer totalDays,
		@Schema(example = "0") Integer totalTimes,
		@Schema(example = "0") Integer totalDistances,
		@Schema(example = "[]") List<PlanTrain> planTrains
	) {
		public record PlanTrain(
			Integer index,
			String trainDate,
			String paramType,
			Integer sessionTime,
			Integer sessionDistance,
			Integer repetition,
			Integer trainParam,
			Integer trainPace,
			Integer interParam
		) {
		}
	}
}
