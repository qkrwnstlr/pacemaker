package com.pacemaker.domain.daily.dto;

import com.pacemaker.domain.report.dto.PlanTrainResponse;

import jakarta.validation.constraints.NotNull;

public record DailyCreateChatRequest(@NotNull String message, @NotNull DailyContext context,
									 @NotNull PlanTrainResponse planTrain, @NotNull String coachTone) {
}
