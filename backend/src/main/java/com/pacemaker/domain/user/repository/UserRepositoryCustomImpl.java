package com.pacemaker.domain.user.repository;

import static com.pacemaker.domain.plan.entity.QPlan.*;
import static com.pacemaker.domain.plan.entity.QPlanTrain.*;
import static com.pacemaker.domain.report.entity.QReport.*;
import static com.pacemaker.domain.report.entity.QReportPlanTrain.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public UserRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public void deleteUserRelatedData(Long userId) {
		queryFactory.delete(reportPlanTrain)
			.where(reportPlanTrain.report.user.id.eq(userId))
			.execute();

		queryFactory.delete(planTrain)
			.where(planTrain.plan.user.id.eq(userId))
			.execute();

		queryFactory.delete(plan)
			.where(plan.user.id.eq(userId))
			.execute();

		queryFactory.delete(report)
			.where(report.user.id.eq(userId))
			.execute();
	}
}
