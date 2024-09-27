package com.pacemaker.domain.plan.repository;

import static com.pacemaker.domain.plan.entity.QPlanTrain.*;
import static com.pacemaker.domain.report.entity.QReport.*;
import static com.pacemaker.domain.report.entity.QReportPlanTrain.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class PlanRepositoryCustomImpl implements PlanRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public PlanRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Integer findSumPlanDistanceByPlanId(Long planId) {
		List<Long> reportIdList = findReportListByPlanId(planId);

		return queryFactory
			.select(report.trainDistance.sum())
			.from(report)
			.where(report.id.in(reportIdList))
			.fetchOne();
	}

	@Override
	public Integer findSumPlanTimeByPlanId(Long planId) {
		List<Long> reportIdList = findReportListByPlanId(planId);

		return queryFactory
			.select(report.trainTime.sum())
			.from(report)
			.where(report.id.in(reportIdList))
			.fetchOne();
	}

	private List<Long> findReportListByPlanId(Long planId) {
		List<Long> planTrainIdList = queryFactory
			.select(planTrain.id)
			.from(planTrain)
			.where(planTrain.plan.id.eq(planId))
			.fetch();

		return queryFactory
			.select(reportPlanTrain.report.id)
			.from(reportPlanTrain)
			.where(reportPlanTrain.planTrain.id.in(planTrainIdList))
			.fetch();
	}
}
