package com.pacemaker.domain.report.dto;

import jakarta.validation.constraints.NotNull;

public record ReportPlanRequestDto(@NotNull String uid, @NotNull Long reportId, @NotNull TrainResultDto trainResult) {
}
