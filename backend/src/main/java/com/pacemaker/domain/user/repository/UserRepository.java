package com.pacemaker.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.report.entity.Report;
import com.pacemaker.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUid(String uid);

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
	List<Report> findMonthlyReports(String uid, Integer year, Integer month); // 사실 report 도메인에 넣어야 할 것 같음

	@Query("""
		select pt
		  from PlanTrain pt
		  where pt.plan.id in (select p.id
								from Plan p
								where p.user.id = (select u.id
												from User u
												where u.uid = :uid))
		    and function('year', pt.trainDate) = :year
		    and function('month', pt.trainDate) = :month
		  order by pt.trainDate
		""")
	List<PlanTrain> findMonthlyPlanTrains(String uid, Integer year,
		Integer month); // plan 도메인에 넣어야 할 것 같음 (PlanTrainRepo~~)
}
