package com.pacemaker.domain.user.dto;

import java.time.LocalDate;
import java.util.List;

import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.report.entity.Report;
import com.pacemaker.domain.report.entity.ReportType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CalendarResponse {

	private LocalDate date;
	private List<TrainingDTO> trainings;

	@Getter
	@Builder
	public static class TrainingDTO {

		private Long id;
		private TrainingType type;

		public static TrainingDTO fromReport(Report report) {
			return TrainingDTO.builder()
				.id(report.getId())
				.type(TrainingType.fromType(report.getReportType()))
				.build();
		}

		public static TrainingDTO fromPlanTrain(PlanTrain planTrain) {
			return TrainingDTO.builder()
				.id(planTrain.getId())
				.type(TrainingType.BEFORE_PLAN_TRAIN)
				.build();
		}
	}

	public enum TrainingType {
		DAILY_REPORT,
		FREE_REPORT,
		PLAN_REPORT,
		BEFORE_PLAN_TRAIN;

		public static TrainingType fromType(ReportType reportType) {
			if (reportType == ReportType.DAILY) {
				return DAILY_REPORT;

			} else if (reportType == ReportType.FREE) {
				return FREE_REPORT;

			} else if (reportType == ReportType.PLAN) {
				return PLAN_REPORT;

			} else {
				throw new IllegalArgumentException("Invalid reportType: " + reportType);
			}
		}
	}
}
