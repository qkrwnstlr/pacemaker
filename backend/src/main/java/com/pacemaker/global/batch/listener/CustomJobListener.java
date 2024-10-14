package com.pacemaker.global.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class CustomJobListener implements JobExecutionListener {
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("********** 배치 작업 시작!! *********");
		System.out.println("[" + jobExecution.getJobInstance().getJobName() + "]");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus().isUnsuccessful()) {
			System.out.println("********** 배치 작업이 실패했습니다. **********");
		} else {
			System.out.println("********** 배치 작업이 성공적으로 완료되었습니다. **********");
		}
	}
}

