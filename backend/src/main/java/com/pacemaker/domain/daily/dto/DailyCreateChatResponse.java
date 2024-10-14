package com.pacemaker.domain.daily.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pacemaker.domain.report.dto.PlanTrainResponse;

import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DailyCreateChatResponse(String message, DailyContext context, PlanTrainResponse planTrain) {
}
