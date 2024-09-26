package com.pacemaker.domain.report.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	Optional<Report> findReportById(Long id);
}
