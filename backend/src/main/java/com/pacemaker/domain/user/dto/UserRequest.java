package com.pacemaker.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserRequest(@NotNull String uid) {
}
