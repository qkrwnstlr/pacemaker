package com.pacemaker.domain.report.dto;

import java.util.List;

public record CreateTrainEvaluationRequest(Integer trainPace, List<SplitData> splitData, List<Integer> heartZone,
										   String coachTone) {
}
