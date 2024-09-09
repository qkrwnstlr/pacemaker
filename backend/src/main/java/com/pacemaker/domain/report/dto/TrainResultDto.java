package com.pacemaker.domain.report.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrainResultDto (@NotNull Integer totalDistance, @NotNull Integer totalTime, @NotNull Integer totalKcal, @NotNull Integer meanPace,
	 Integer meanHeartRate, Integer meanCadence, Integer gradePace, Integer gradeHeartRate, Integer gradeCadence,
	 @NotNull String trainMap, @Size(min = 5, max = 5) List<Integer> heartRateZone, List<String> coachText) {
}

