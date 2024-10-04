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

	/*
	 * 나중에 아래 쿼리랑 같은지 확인하기
		select pt
		  from PlanTrain pt
		  join pt.plan p
		  where p.user.uid = :uid
			and pt.status = "BEFORE"
			and function('year', pt.trainDate) = :year
			and function('month', pt.trainDate) = :month
		  order by pt.trainDate
	 */
	@Query("""
		select pt
		  from PlanTrain pt
		  where pt.plan.id in (select p.id
								 from Plan p
								 where p.user.uid = :uid)
			and pt.status = "BEFORE"
		    and function('year', pt.trainDate) = :year
		    and function('month', pt.trainDate) = :month
		  order by pt.trainDate
		""")
	List<PlanTrain> findMonthlyBeforePlanTrains(String uid, Integer year, Integer month);

	/*
	 * 1. and절과 on절을 사용해서의 차이는 거의 없다! -> 쿼리 최적화를 DB 최적화 엔진이 해주기 때문에 동일한 join 전략이 된다고 함
	 * 2. JPQL에서는 가독성 측면에서 내부 조인의 경우 where절에 작성한다!
	 * 3. JPQL에서 외부 조인일 경우에 on절을 사용하여 조인 조건을 명확히 정의하고 where절을 통해 추가적인 필터링을 한다고 함.
	 * 4. join fetch를 안 써도 되는 이유는 service에서 해당 객체에 접근을 하는 것 때문에 LAZY를 달고 join fetch를 했던 것이지만
	 *    이 경우에는 "스칼라 반환"을 하므로 join만 사용해도 된다!!
	 * 5. 기존처럼 count(pt) 와 같은 것으로 하는 것보다 exists를 사용하여 판단하면 count를 안 해도 되기 때문에 쿼리가 최적화가 된다!
	 *
	@Query("""
		select exists (select 1
		  				 from PlanTrain pt
		  				 join pt.plan p
		  				   on p.user.id = :userId
		  				   and pt.trainDate = current_date)
		""")
	 */
	@Query("""
		select exists (select 1
		  				 from PlanTrain pt
		  				 join pt.plan p
		  				 where p.user.id = :userId
		  				   and pt.trainDate = current_date)
		""")
	boolean existsNowPlanTrain(Long userId);
}
