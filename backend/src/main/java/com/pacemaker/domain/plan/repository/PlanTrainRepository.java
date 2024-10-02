package com.pacemaker.domain.plan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.plan.entity.PlanTrain;

@Repository
public interface PlanTrainRepository extends JpaRepository<PlanTrain, Long>, PlanTrainRepositoryCustom {

	Optional<PlanTrain> findPlanTrainById(Long id);

	@Query("""
		select pt
		  from PlanTrain pt
		  where pt.plan.id in (select p.id
								from Plan p
								where p.user.id = (select u.id
												from User u
												where u.uid = :uid))
			and pt.status = "BEFORE"
		    and function('year', pt.trainDate) = :year
		    and function('month', pt.trainDate) = :month
		  order by pt.trainDate
		""")
	List<PlanTrain> findMonthlyBeforePlanTrains(String uid, Integer year, Integer month);

	@Query("""
		select count(pt) > 0
		  from PlanTrain pt
		  join fetch Plan p
		    on pt.plan.id = p.id
		    and p.user.id = :userId
		  where pt.trainDate = current_date
		""")
	boolean existsNowPlanTrain(Long userId);
}
