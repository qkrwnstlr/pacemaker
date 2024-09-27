package com.pacemaker.domain.plan.dto;

import com.pacemaker.domain.report.dto.PlanTrainResponse;

import lombok.Builder;

@Builder
public record ActivePlanTrainResponse(PlanTrainResponse planTrain) {
}
