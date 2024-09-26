package com.pacemaker.domain.plan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.plan.entity.PlanTrain;

@Repository
public interface PlanTrainRepository extends JpaRepository<PlanTrain, Long>, PlanTrainRepositoryCustom {

	Optional<PlanTrain> findPlanTrainById(Long id);
}
