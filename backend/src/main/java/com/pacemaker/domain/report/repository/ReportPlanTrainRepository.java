package com.pacemaker.domain.report.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.report.entity.ReportPlanTrain;

@Repository
public interface ReportPlanTrainRepository extends JpaRepository<ReportPlanTrain, Long> {

	Optional<ReportPlanTrain> findReportPlanTrainByReportId(Long reportId);
}
