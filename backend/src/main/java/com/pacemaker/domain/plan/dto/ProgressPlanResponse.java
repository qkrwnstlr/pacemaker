package com.pacemaker.domain.plan.dto;

import com.google.gson.Gson;
import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.entity.PlanStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProgressPlanResponse {

	private Long id;

	private Integer totalDistances;

	private Integer totalTimes;

	private Integer totalDays;

	private Integer completedCount;

	private PlanStatus status;

	private Context context;

	@Getter
	private static class Context {
		private String goal;

		private Integer goalTime;

		private Integer goalDistance;
	}

	@Builder
	public ProgressPlanResponse(Plan plan) {
		this.id = plan.getId();
		this.totalDistances = plan.getTotalDistances();
		this.totalTimes = plan.getTotalTimes();
		this.totalDays = plan.getTotalDays();
		this.completedCount = plan.getCompletedCount();
		this.status = plan.getStatus();
		this.context = new Gson().fromJson(plan.getContext(), Context.class);
	}
}
