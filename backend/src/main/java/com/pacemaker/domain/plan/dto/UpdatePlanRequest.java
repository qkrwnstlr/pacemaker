package com.pacemaker.domain.plan.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePlanRequest(@NotNull String uid, @NotNull ContentRequest.Plan plan) {
}
