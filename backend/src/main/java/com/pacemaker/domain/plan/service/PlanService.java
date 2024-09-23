package com.pacemaker.domain.plan.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pacemaker.domain.plan.entity.Plan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

	// 플랜 생성 부분 만들자!
	@Transactional
	public void createPlan(Plan plan) {

	}
}
