package com.pacemaker.domain.plan.dto;

import java.time.LocalDate;

import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.entity.TrainStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanTrainResponse {

	private Long id;

	private LocalDate trainDate;

	private String paramType;

	private Integer sessionTime;

	private Integer sessionDistance;

	private Integer repetition;

	private Integer trainParam;

	private Integer trainPace;

	private Integer interParam;

	private TrainStatus status;

	private Integer index;

	@Builder
	public PlanTrainResponse(PlanTrain planTrain, int index) {
		this.id = planTrain.getId();
		this.trainDate = planTrain.getTrainDate();
		this.paramType = planTrain.getParamType();
		this.sessionTime = planTrain.getSessionTime();
		this.sessionDistance = planTrain.getSessionDistance();
		this.repetition = planTrain.getRepetition();
		this.trainParam = planTrain.getTrainParam();
		this.trainPace = planTrain.getTrainPace();
		this.interParam = planTrain.getInterParam();
		this.status = planTrain.getStatus();
		this.index = index;
	}
}
