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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PlanTrain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@Column(name = "train_date", nullable = false)
	private LocalDate trainDate;

	@Column(name = "param_type", nullable = false)
	private String paramType;

	@Column(name = "train_time", nullable = false)
	private Integer trainTime;

	@Column(name = "train_distance", nullable = false)
	private Integer trainDistance;

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
	private TrainStatus status;

	@Builder
	public PlanTrain(Plan plan, LocalDate trainDate, String paramType, Integer trainTime, Integer trainDistance,
		Integer repetition, Integer trainParam, Integer trainPace, Integer interParam, TrainStatus status) {
		this.plan = plan;
		this.trainDate = trainDate;
		this.paramType = paramType;
		this.trainTime = trainTime;
		this.trainDistance = trainDistance;
		this.repetition = repetition;
		this.trainParam = trainParam;
		this.trainPace = trainPace;
		this.interParam = interParam;
		this.status = (status != null) ? status : TrainStatus.BEFORE;
	}
}
