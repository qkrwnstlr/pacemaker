package com.pacemaker.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record GoogleLoginRequest(@NotNull String name) {
}
