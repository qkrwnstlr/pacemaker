package com.pacemaker.domain.report.entity;

import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.plan.entity.PlanDetail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@IdClass(ReportPlanDetailId.class) // 복합키
@Getter
@NoArgsConstructor
public class ReportPlanDetail {
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private Report report;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_detail_id", nullable = false)
	private PlanDetail planDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coach_id", nullable = false)
	private Coach coach;

	@Lob
	@Column(name = "coach_message", columnDefinition = "TEXT")
	private String coachMessage;

	@Builder
	public ReportPlanDetail(Report report, PlanDetail planDetail, Coach coach, String coachMessage) {

		this.report = report;
		this.planDetail = planDetail;
		this.coach = coach;
		this.coachMessage = coachMessage;
	}
}
