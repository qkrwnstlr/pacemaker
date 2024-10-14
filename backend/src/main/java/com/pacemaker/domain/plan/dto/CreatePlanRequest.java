package com.pacemaker.domain.plan.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePlanRequest(@NotNull String uid, @NotNull ContentRequest.Context context,
								@NotNull ContentRequest.Plan plan) {
}
