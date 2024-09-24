package com.pacemaker.domain.plan.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePlanRequest(@NotNull String uid, @NotNull ContentRequest contentRequest) {
}
