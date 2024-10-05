package com.pacemaker.global.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pacemaker.global.exception.ScheduledTaskException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlanScheduler {

	private final JobLauncher jobLauncher;  // 배치 작업 실행을 위한 런처
	private final Job planPostponeJob;  // Job을 주입받음

	// 배치 작업을 24시마다 실행
	@Scheduled(cron = "0 0 0 * * *")  // 매일 24시
	public void runPlanPostponeJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())  // 매 실행마다 고유한 파라미터
				.toJobParameters();
			jobLauncher.run(planPostponeJob, jobParameters);  // 배치 작업 실행

		} catch (Exception e) {
			throw new ScheduledTaskException("스케줄링 작업 중 오류 발생", e);
		}
	}
}
