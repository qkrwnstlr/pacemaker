package com.pacemaker.domain.plan.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PlanTrain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@Column(name = "train_date", nullable = false)
	private LocalDate trainDate;

	@Column(name = "param_type", nullable = false)
	private String paramType;

	@Column(name = "session_time", nullable = false)
	private Integer sessionTime;

	@Column(name = "session_distance", nullable = false)
	private Integer sessionDistance;

	@Column(nullable = false)
	private Integer repetition;

	@Column(name = "train_param", nullable = false)
	private Integer trainParam;

	@Column(name = "train_pace", nullable = false)
	private Integer trainPace;

	@Column(name = "inter_param", nullable = false)
	private Integer interParam;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PlanTrainStatus status;

	@Builder
	public PlanTrain(Plan plan, LocalDate trainDate, String paramType, Integer sessionTime, Integer sessionDistance,
		Integer repetition, Integer trainParam, Integer trainPace, Integer interParam, PlanTrainStatus status) {
		this.plan = plan;
		this.trainDate = trainDate;
		this.paramType = paramType;
		this.sessionTime = sessionTime;
		this.sessionDistance = sessionDistance;
		this.repetition = repetition;
		this.trainParam = trainParam;
		this.trainPace = trainPace;
		this.interParam = interParam;
		this.status = (status != null) ? status : PlanTrainStatus.BEFORE;
	}

	public void updatePlanTrainStatus(PlanTrainStatus status) {
		this.status = status;
	}

	public void updateTrainDate(LocalDate trainDate) {
		this.trainDate = trainDate;
	}

	// 연관관계 편의 메서드
	// public void setPlan(Plan plan) { // lombok으로 대체
	// 	this.plan = plan;
	// }
}
