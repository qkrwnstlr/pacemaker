package com.pacemaker.domain.report.dto;

import jakarta.validation.constraints.NotNull;

public record ReportPlanRequest(@NotNull String uid, @NotNull Long reportId, @NotNull TrainResult trainResult) {
}
