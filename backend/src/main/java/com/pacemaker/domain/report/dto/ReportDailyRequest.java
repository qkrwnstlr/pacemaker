package com.pacemaker.domain.report.dto;

import jakarta.validation.constraints.NotNull;

public record ReportDailyRequest(@NotNull String uid, @NotNull TrainResult trainResult) {
}
