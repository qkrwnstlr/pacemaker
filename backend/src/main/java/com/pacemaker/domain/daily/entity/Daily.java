package com.pacemaker.domain.daily.entity;

import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.report.entity.Report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Daily {
	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private Report report;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coach_id", nullable = false)
	private Coach coach;

	@Column(name = "param_type", nullable = false)
	private String paramType;

	@Column(name = "train_time", nullable = false)
	private Integer trainTime;

	@Column(name = "train_distance", nullable = false)
	private Integer trainDistance;

	@Lob
	@Column(name = "context", nullable = false, columnDefinition = "TEXT")
	private String context;

	@Column(nullable = false)
	private Integer repetition;

	@Column(name = "train_param", nullable = false)
	private Integer trainParam;

	@Column(name = "train_pace", nullable = false)
	private Integer trainPace;

	@Column(name = "inter_param", nullable = false)
	private Integer interParam;

	@Lob
	@Column(name = "coach_message", columnDefinition = "TEXT")
	private String coachMessage;

	@Builder
	public Daily(Report report, Coach coach, String paramType, Integer trainTime, Integer trainDistance, String context,
		Integer repetition, Integer trainParam, Integer trainPace, Integer interParam, String coachMessage) {
		this.report = report;
		this.coach = coach;
		this.paramType = paramType;
		this.trainTime = trainTime;
		this.trainDistance = trainDistance;
		this.context = context;
		this.repetition = repetition;
		this.trainParam = trainParam;
		this.trainPace = trainPace;
		this.interParam = interParam;
		this.coachMessage = coachMessage;
	}
}
