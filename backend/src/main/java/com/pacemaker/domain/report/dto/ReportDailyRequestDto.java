package com.pacemaker.domain.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportDailyRequestDto {

	@NotNull
	private String uid;

	@NotNull
	private TrainResultDto trainResult;
}
