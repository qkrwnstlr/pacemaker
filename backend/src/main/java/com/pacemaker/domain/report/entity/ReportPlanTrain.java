package com.pacemaker.domain.report.entity;

import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.plan.entity.PlanTrain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@IdClass(ReportPlanTrainId.class) // 복합키
@Getter
@NoArgsConstructor
public class ReportPlanTrain {
	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private Report report;

	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_train_id", nullable = false)
	private PlanTrain planTrain;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coach_id", nullable = false)
	private Coach coach;

	@Lob
	@Column(name = "coach_message", columnDefinition = "TEXT")
	private String coachMessage;

	@Builder
	public ReportPlanTrain(Report report, PlanTrain planTrain, Coach coach, String coachMessage) {
		this.report = report;
		this.planTrain = planTrain;
		this.coach = coach;
		this.coachMessage = coachMessage;
	}
}
