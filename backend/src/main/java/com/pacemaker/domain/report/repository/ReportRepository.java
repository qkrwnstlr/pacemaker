package com.pacemaker.domain.report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	Optional<Report> findReportById(Long id);

	@Query("""
		select r
		  from Report r
		  where r.user.id = (select u.id
		  					    from User u
		  					    where u.uid = :uid)
		    and function('year', r.trainDate) = :year
		    and function('month', r.trainDate) = :month
		  order by r.trainDate
		""")
	List<Report> findMonthlyReports(String uid, Integer year, Integer month);
}
