package com.pacemaker.domain.plan.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.plan.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryCustom {

	@Query("""
			select count(p) > 0
			  from Plan p
			  where p.user.id = :userId
			    and p.status = 'ACTIVE'
			""")
	boolean existsActivePlan(Long userId);

	/*
	 * 나중에 바꿔서 테스트 해 볼것!
	 * 이렇게 하면 user를 join하는 건 없지만 되는지 궁금함
	 * 1. 아마도 user정보를 사용하지 않는 거라면 경로 탐색? 을 통해서 조건을 사용한다고 해서 join fetch가 필요 없는 것으로 판단됨
	 * 2. 즉 잘 동작할 것이다!!
	@Query("""
			select p
			  from Plan p
			  join fetch p.planTrains pt
			  where p.user.uid = :uid
			    and p.status = 'ACTIVE'
			  order by pt.trainDate
			""")

	 */
	@Query("""
			select p
			  from Plan p
			  join fetch p.planTrains pt
			  where p.user.id = (select u.id
			  					   from User u
			  					   where u.uid = :uid)
			    and p.status = 'ACTIVE'
			  order by pt.trainDate
			""")
	Optional<Plan> findActivePlan(String uid); // @Param("uid")를 해주면 명시적이라 좋기도 하지만 생략도 가능!
	Optional<Plan> findPlanById(Long planId);

	@Query("""
			select p
			  from Plan p
			  where p.user.id = (select u.id
			  					   from User u
			  					   where u.uid = :uid)
			    and (:date between p.createdAt and p.expiredAt)
			""")
	Optional<Plan> findPlanByUidAndDate(String uid, LocalDate date);
}
