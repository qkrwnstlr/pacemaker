package com.pacemaker.domain.report.repository;

import static com.pacemaker.domain.report.entity.QReportPlanTrain.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class ReportPlanTrainRepositoryCustomImpl implements ReportPlanTrainRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	public ReportPlanTrainRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public boolean reportExists(Long id) {
		return queryFactory
			.selectFrom(reportPlanTrain)
			.where(reportPlanTrain.planTrain.id.eq(id))
			.fetchFirst() != null;
	}
}
