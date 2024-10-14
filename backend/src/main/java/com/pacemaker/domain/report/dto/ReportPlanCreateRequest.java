package com.pacemaker.domain.report.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ReportPlanCreateRequest(@NotNull String uid, @NotNull Long planTrainId, @NotNull Long coachNumber,
									  @NotNull LocalDateTime trainDate, @NotNull TrainResult trainResult) {
}
