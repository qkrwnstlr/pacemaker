package com.pacemaker.domain.user.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(@NotNull String name, @NotNull Integer age, @NotNull Integer height,
								@NotNull Integer weight, @NotNull String gender, @NotNull List<String> injuries) {
}
