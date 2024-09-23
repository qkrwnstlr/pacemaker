package com.pacemaker.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(@NotNull String uid, @NotNull Integer age, @NotNull Integer height,
								@NotNull Integer weight) {
}
