package com.pacemaker.domain.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.plan.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

	boolean existsByUserId(Long userId);
}
