package com.pacemaker.domain.report.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pacemaker.domain.report.entity.Report;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TrainResult(@NotNull Integer trainDistance, @NotNull Integer trainTime, @NotNull Integer heartRate,
						  @NotNull Integer pace, @NotNull Integer cadence, @NotNull Integer kcal,
						  @NotNull List<Integer> heartZone, @NotNull List<SplitData> splitData,
						  @NotNull List<List<Double>> trainMap, List<String> coachMessage) {

	public static TrainResult of(Report report, List<String> coachMessage) throws JsonProcessingException {
		return TrainResult.builder()
			.trainDistance(report.getTrainDistance())
			.trainTime(report.getTrainTime())
			.heartRate(report.getHeartRate())
			.pace(report.getPace())
			.cadence(report.getCadence())
			.kcal(report.getKcal())
			.heartZone(convertListHeartZone(report.getHeartZone()))
			.splitData(convertObjectSplitData(report.getSplitData()))
			.trainMap(convertListTrainMap(report.getTrainMap()))
			.coachMessage(coachMessage)
			.build();
	}

	private static List<Integer> convertListHeartZone(String heartZone) {
		String heartZones = heartZone.substring(1, heartZone.length() - 1);
		return Arrays.stream(heartZones.split(",")).map(Integer::parseInt).collect(Collectors.toList());
	}

	private static List<SplitData> convertObjectSplitData(String splitData) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(splitData, new TypeReference<List<SplitData>>() {});
	}

	private static List<List<Double>> convertListTrainMap(String trainMap) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper.readValue(trainMap, new TypeReference<>() {
		});
	}
}
