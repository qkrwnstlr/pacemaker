package com.pacemaker.domain.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SplitData(@JsonProperty("gradeDistance") Integer gradeDistance,
						@JsonProperty("gradePace") Integer gradePace,
						@JsonProperty("gradeHeartRate") Integer gradeHeartRate,
						@JsonProperty("gradeCadence") Integer gradeCadence) {
}
