package com.pacemaker.domain.report.entity;

import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.train.entity.Train;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ReportDaily {
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private Report report;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "train_id", nullable = false)
	private Train train;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coach_id", nullable = false)
	private Coach coach;

	@Column(name = "goal_distance", nullable = false, columnDefinition = "int default 0")
	private Integer goalDistance;

	@Column(name = "goal_time", nullable = false, columnDefinition = "int default 0")
	private Integer goalTime;

	@Column(name = "total_time", nullable = false)
	private Integer totalTime;

	@Lob
	@Column(name = "train_content", nullable = false, columnDefinition = "TEXT")
	private String trainContent;

	@Lob
	@Column(name = "coach_message", columnDefinition = "TEXT")
	private String coachMessage;

	@Builder
	public ReportDaily(Train train, Coach coach, Integer goalDistance, Integer goalTime, Integer totalTime,
		String trainContent, String coachMessage) {

		this.train = train;
		this.coach = coach;
		this.goalDistance = goalDistance;
		this.goalTime = goalTime;
		this.totalTime = totalTime;
		this.trainContent = trainContent;
		this.coachMessage = coachMessage;
	}
}
