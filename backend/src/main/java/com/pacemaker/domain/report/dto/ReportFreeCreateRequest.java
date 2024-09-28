package com.pacemaker.domain.report.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ReportFreeCreateRequest(@NotNull String uid, @NotNull LocalDateTime trainDate,
									  @NotNull TrainResult trainResult) {
}
