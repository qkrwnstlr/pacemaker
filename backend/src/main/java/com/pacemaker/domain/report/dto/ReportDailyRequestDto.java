package com.pacemaker.domain.report.dto;

import jakarta.validation.constraints.NotNull;

public record ReportDailyRequestDto(@NotNull String uid, @NotNull TrainResultDto trainResult) {
}
