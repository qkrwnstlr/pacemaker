package com.pacemaker.global.batch.listener;

import org.springframework.batch.core.SkipListener;

import com.pacemaker.domain.plan.entity.Plan;

public class CustomSkipListener implements SkipListener<Plan, Plan> {

	@Override
	public void onSkipInRead(Throwable t) {
		// 읽기 중 스킵된 경우의 처리
		System.out.println("Skipped in read: " + t.getMessage());
	}

	@Override
	public void onSkipInProcess(Plan item, Throwable t) {
		// 처리 중 스킵된 경우의 처리
		System.out.println("Skipped in process: " + item + " due to: " + t.getMessage());
	}

	@Override
	public void onSkipInWrite(Plan item, Throwable t) {
		// 쓰기 중 스킵된 경우의 처리
		System.out.println("Skipped in write: " + item + " due to: " + t.getMessage());
	}
}
