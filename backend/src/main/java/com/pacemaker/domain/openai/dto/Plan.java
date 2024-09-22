package com.pacemaker.domain.openai.dto;

import java.util.List;

public record Plan(int totalDays, int totalTimes, List<PlanDetail> trainDetails) {
}
