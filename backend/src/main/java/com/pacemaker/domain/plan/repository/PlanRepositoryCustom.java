package com.pacemaker.domain.plan.repository;

public interface PlanRepositoryCustom {
	Integer findSumPlanDistanceByPlanId(Long planId);
	Integer findSumPlanTimeByPlanId(Long planId);
}
