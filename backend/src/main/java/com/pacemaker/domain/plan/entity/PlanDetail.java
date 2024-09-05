package com.pacemaker.domain.plan.entity;

import java.time.LocalDateTime;

import com.pacemaker.domain.train.entity.Train;
import com.pacemaker.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PlanDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "train_id", nullable = false)
	private Train train;

	@Column(name = "train_date", nullable = false, columnDefinition = "datetime default now()")
	private LocalDateTime trainDate;

	@Column(name = "total_time", nullable = false)
	private Integer trainTime;

	@Lob
	@Column(name = "train_content", nullable = false, columnDefinition = "TEXT")
	private String trainContent;

	@Enumerated(EnumType.STRING)
	private Feedback feedback;

	@Builder
	public PlanDetail(Plan plan, Train train, LocalDateTime trainDate, Integer trainTime, String trainContent,
		Feedback feedback) {

		this.plan = plan;
		this.train = train;
		this.trainDate = trainDate;
		this.trainTime = trainTime;
		this.trainContent = trainContent;
		this.feedback = feedback;
	}
}
