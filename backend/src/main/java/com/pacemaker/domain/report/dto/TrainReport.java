package com.pacemaker.domain.report.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.Builder;

@Builder
public record TrainReport(List<LocalTime> trainDuration, TrainResult trainResult, TrainEvaluation trainEvaluation) {
}
