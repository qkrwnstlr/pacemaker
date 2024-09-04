package com.pacemaker.domain.report.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ReportPlanDetailId implements Serializable {
	private Long report;
	private Long planDetail;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReportPlanDetailId that = (ReportPlanDetailId) o;
		return Objects.equals(report, that.report) &&
			Objects.equals(planDetail, that.planDetail);
	}

	@Override
	public int hashCode() {
		return Objects.hash(report, planDetail);
	}
}
