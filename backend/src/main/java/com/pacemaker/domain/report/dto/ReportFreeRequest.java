package com.pacemaker.domain.report.dto;

import jakarta.validation.constraints.NotNull;

public record ReportFreeRequest(@NotNull String uid, @NotNull TrainResult trainResult) {
};
