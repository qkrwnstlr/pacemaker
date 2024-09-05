package com.pacemaker.domain.report.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainResultDto {

	@NotNull
	private Integer totalDistance;

	@NotNull
	private Integer totalTime;

	@NotNull
	private Integer totalKcal;

	@NotNull
	private Integer meanPace;

	private Integer meanHeartRate;

	private Integer meanCadence;

	private Integer gradePace;

	private Integer gradeHeartRate;

	private Integer gradeCadence;

	@NotNull
	private String trainMap;

	@Size(min = 5, max = 5)
	private List<Integer> heartRateZone;

	private List<String> coachText;
}
