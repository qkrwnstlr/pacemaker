package com.pacemaker.domain.report.dto;

import lombok.Builder;

@Builder
public record ReportPlanCreateResponse(PlanTrainResponse planTrainResponse, TrainReport trainReport) {
}
