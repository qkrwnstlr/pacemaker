package com.pacemaker.domain.user.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.entity.TrainStatus;
import com.pacemaker.domain.report.entity.Report;
import com.pacemaker.domain.report.entity.ReportType;

import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalenderResponse {

	private LocalDate date;
	private List<TrainingDTO> trainings;

	@Builder
	public static class TrainingDTO {

		private TrainingType type;
		private Long planTrainId;
		private Long reportId;

		public static TrainingDTO fromPlanTrain(PlanTrain planTrain) {
			return TrainingDTO.builder()
				.type(TrainingType.valueOf(planTrain.getStatus().name()))
				.planTrainId(planTrain.getId())
				.build();
		}

		public static TrainingDTO fromReport(Report report) {
			return TrainingDTO.builder()
				.type(TrainingType.valueOf(report.getReportType().name()))
				.reportId(report.getId())
				.build();
		}
	}

	@Getter
	public enum TrainingType {
		
		REPORT_TYPE_PLAN(ReportType.PLAN),
		REPORT_TYPE_DAILY(ReportType.DAILY),
		REPORT_TYPE_FREE(ReportType.FREE),
		PLAN_TRAIN_BEFORE(TrainStatus.BEFORE),
		PLAN_TRAIN_DONE(TrainStatus.DONE);

		private final TrainStatus trainStatus;
		private final ReportType reportType;

		// 생성자
		TrainingType(TrainStatus trainStatus) {
			this.trainStatus = trainStatus;
			this.reportType = null;
		}

		TrainingType(ReportType reportType) {
			this.reportType = reportType;
			this.trainStatus = null;
		}
	}
}
