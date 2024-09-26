package com.pacemaker.domain.plan.repository;

import static com.pacemaker.domain.plan.entity.QPlanTrain.*;

import java.util.List;

import com.pacemaker.domain.plan.entity.PlanTrain;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class PlanTrainRepositoryCustomImpl implements PlanTrainRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public PlanTrainRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Integer findIndexByPlanTrainId(Long planTrainId) {
		Long planId = queryFactory
			.select(planTrain.plan.id)
			.from(planTrain)
			.where(planTrain.id.eq(planTrainId))
			.fetchOne();

		if (planId == null) {
			return -1;
		}

		List<PlanTrain> planTrainList = queryFactory
			.selectFrom(planTrain)
			.where(planTrain.plan.id.eq(planId))
			.orderBy(planTrain.trainDate.asc())
			.fetch();

		for (int i = 0; i < planTrainList.size(); i++) {
			if (planTrainList.get(i).getId().equals(planTrainId)) {
				return i + 1;
			}
		}

		return -1;
	}
}
