package com.pacemaker.domain.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrainEvaluation(@JsonProperty("paceEvaluation") Integer paceEvaluation,
							  @JsonProperty("heartRateEvaluation") Integer heartRateEvaluation,
							  @JsonProperty("cadenceEvaluation") Integer cadenceEvaluation) {
}
