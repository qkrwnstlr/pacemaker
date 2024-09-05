package com.pacemaker.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(@NotNull String uid, @NotNull String name, @NotNull Integer year,
								@NotNull Integer gender, @NotNull Integer height, @NotNull Integer weight,
								Float distance, Integer minute, Integer pace, Integer cadence) {
}
