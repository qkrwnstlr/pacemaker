package com.pacemaker.domain.realtime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RealTimeResponse(@NotNull String textFeedback, @NotNull String textCheer) {
}
