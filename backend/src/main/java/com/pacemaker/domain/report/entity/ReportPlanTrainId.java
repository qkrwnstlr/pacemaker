package com.pacemaker.domain.report.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ReportPlanTrainId implements Serializable {
	private Long report;
	private Long planTrain;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReportPlanTrainId that = (ReportPlanTrainId) o;
		return Objects.equals(report, that.report) &&
			Objects.equals(planTrain, that.planTrain);
	}

	@Override
	public int hashCode() {
		return Objects.hash(report, planTrain);
	}
}
