package com.pacemaker.domain.coach.dto;

import jakarta.validation.constraints.NotNull;

public record CoachUpdateRequest(@NotNull Long coachNumber) {
}
