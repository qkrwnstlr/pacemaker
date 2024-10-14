package com.pacemaker.domain.daily.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DailyContext(@NotNull String goal, @NotNull Integer goalTime, @NotNull Integer goalDistance,
						   @NotNull UserInfo userInfo) {

	@Builder
	public record UserInfo(@NotNull Integer age, @NotNull Integer height, @NotNull Integer weight,
						   @NotNull String gender, @NotNull List<String> injuries, @NotNull Integer recentRunPace,
						   @NotNull Integer recentRunDistance, @NotNull Integer recentRunHeartRate) {
	}
}
