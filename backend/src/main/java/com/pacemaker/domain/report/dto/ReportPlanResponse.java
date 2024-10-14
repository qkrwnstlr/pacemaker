package com.pacemaker.domain.report.dto;

import lombok.Builder;

@Builder
public record ReportPlanResponse(PlanTrainResponse planTrain, TrainReport trainReport) {
}
