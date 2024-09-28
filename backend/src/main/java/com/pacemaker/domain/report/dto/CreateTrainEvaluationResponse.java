package com.pacemaker.domain.report.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record CreateTrainEvaluationResponse(TrainEvaluation trainEvaluation, List<String> coachMessage) {
}
