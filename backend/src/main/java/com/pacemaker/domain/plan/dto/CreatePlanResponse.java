package com.pacemaker.domain.plan.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.entity.PlanStatus;
import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.entity.TrainStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePlanResponse {

	private Long id;

	private LocalDate createdAt;

	private LocalDate expiredAt;

	private Integer totalDays;

	private Integer totalTimes;

	private Integer totalDistances;

	private ContextDTO context;

	private Integer completedCount;

	private PlanStatus status;

	private List<PlanTrainDTO> planTrains = new ArrayList<>();

	public void addPlanTrain(PlanTrain planTrain, int index) {
		planTrains.add(PlanTrainDTO.builder()
			.planTrain(planTrain)
			.index(index)
			.build()
		);
	}

	@Builder
	public CreatePlanResponse(Plan plan) {
		this.id = plan.getId();
		this.createdAt = plan.getCreatedAt();
		this.expiredAt = plan.getExpiredAt();
		this.totalDays = plan.getTotalDays();
		this.totalTimes = plan.getTotalTimes();
		this.totalDistances = plan.getTotalDistances();
		this.context = new Gson().fromJson(plan.getContext(), ContextDTO.class);
		this.completedCount = plan.getCompletedCount();
		this.status = plan.getStatus();
	}

	@Getter
	private static class ContextDTO {
		private String goal;

		private Integer goalTime;

		private Integer goalDistance;

		private List<String> trainDayOfWeek;
	}

	@Getter
	private static class PlanTrainDTO {
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
		private PlanTrainDTO(PlanTrain planTrain, int index) {
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
}
