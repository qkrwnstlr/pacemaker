package com.pacemaker.global.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import com.pacemaker.domain.plan.service.PlanService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
// @EnableBatchProcessing  //  배치 기능 활성 -> extends DefaultBatchConfiguration으로 대체 (batch v5)
public class BatchConfig extends DefaultBatchConfiguration {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final PlanService planService;
	private final JobLauncher jobLauncher;  // 배치 작업 실행을 위한 런처
	private final Job planUpdateJob;  // Job을 직접 주입받음

	// 배치 작업을 24시마다 실행
	@Scheduled(cron = "0 0 0 * * *")  // 매일 24시
	public void runPlanUpdateJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())  // 매 실행마다 고유한 파라미터
				.toJobParameters();
			jobLauncher.run(planUpdateJob, jobParameters);  // 배치 작업 실행
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Bean
	public Job planUpdateJob() {
		return new JobBuilder("planUpdateJob", jobRepository)
			.start(updatePlanStep())
			.build();
	}

	@Bean
	public Step updatePlanStep() {
		return new StepBuilder("updatePlanStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				planService.updatePlans();  // 서비스에서 실제 로직 처리
				return null;
			}, transactionManager)
			.build();
	}
}
