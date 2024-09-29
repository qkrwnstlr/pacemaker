package com.pacemaker.domain.realtime.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RealTimeTtsRequest(@NotNull Long coachNumber, @NotNull String message) {
}
