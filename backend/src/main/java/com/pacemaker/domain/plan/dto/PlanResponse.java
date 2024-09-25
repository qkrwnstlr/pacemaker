package com.pacemaker.domain.plan.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.entity.PlanStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanResponse {

	private Long id;

	private LocalDate createdAt;

	private LocalDate expiredAt;

	private Integer totalDays;

	private Integer totalTimes;

	private Integer totalDistances;

	private String context;

	private Integer completedCount;

	private PlanStatus status;

	private List<PlanTrainResponse> planTrains = new ArrayList<>();

	@Builder
	public PlanResponse(Plan plan) {
		this.id = plan.getId();
		this.createdAt = plan.getCreatedAt();
		this.expiredAt = plan.getExpiredAt();
		this.totalDays = plan.getTotalDays();
		this.totalTimes = plan.getTotalTimes();
		this.totalDistances = plan.getTotalDistances();
		this.context = plan.getContext();
		this.completedCount = plan.getCompletedCount();
		this.status = plan.getStatus();
	}
}
