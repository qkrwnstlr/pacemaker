package com.pacemaker.domain.plan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.plan.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

	boolean existsByUserId(Long userId);

	@Query("""
			select p
			  from Plan p
			  join fetch p.planTrains pt
			  where p.user.id in (select u.id
			  					    from User u
			  					    where u.uid = :uid)
			    and p.status = 'ACTIVE'
			  order by pt.trainDate
			""")
	Optional<Plan> findActivePlan(String uid); // @Param("uid")를 해주면 명시적이라 좋기도 하지만 생략도 가능!

}
