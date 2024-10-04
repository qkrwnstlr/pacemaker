package com.pacemaker.global.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.service.PlanService;
import com.pacemaker.global.batch.listener.CustomSkipListener;
import com.pacemaker.global.batch.listener.CustomStepListener;
import com.pacemaker.global.exception.ScheduledTaskException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
// @EnableBatchProcessing  //  배치 기능 활성 -> extends DefaultBatchConfiguration으로 대체 (batch v5)
public class BatchConfig extends DefaultBatchConfiguration {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final PlanService planService;
	private final JobLauncher jobLauncher;  // 배치 작업 실행을 위한 런처
	private final Job planPostponeJob;  // Job을 직접 주입받음

	// 배치 작업을 24시마다 실행
	@Scheduled(cron = "0 0 0 * * *")  // 매일 24시
	public void runPlanPostponeJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())  // 매 실행마다 고유한 파라미터
				.toJobParameters();
			jobLauncher.run(planPostponeJob, jobParameters);  // 배치 작업 실행

		} catch (Exception e) {
			throw new ScheduledTaskException("스케줄링 작업 중 오류 발생");
		}
	}

	@Bean
	public Job planPostponeJob() {
		return new JobBuilder("planPostponeJob", jobRepository)
			.start(planPostponeStep())
			.build();
	}

	@Bean
	public Step planPostponeStep() {
		return new StepBuilder("planPostponeStep", jobRepository)
			.<Plan, Plan>chunk(10, transactionManager)
			.reader(activePlanReader())
			.processor(planPostponeProcessor())
			.writer(planPostponeWriter())
			.listener(new CustomStepListener()) // 스텝 리스너 등록
			.listener(new CustomSkipListener()) // 스킵 리스너 등록
			.faultTolerant()
			.skip(Exception.class)
			.skipLimit(10)
			.build();
	}

	@Bean
	public ItemReader<Plan> activePlanReader() {
		return new ListItemReader<>(planService.getPlansForPostpone());  // ListItemReader로 List<Plan> 처리
	}

	@Bean
	public ItemProcessor<Plan, Plan> planPostponeProcessor() {
		return new ItemProcessor<Plan, Plan>() {
			@Override
			public Plan process(@NonNull Plan plan) throws Exception {
				planService.planPostponeAndPlanTrain(plan);
				return plan;
			}
		};

		// 람다식 표현
		// return plan -> {
		// 	planService.planPostponeAndPlanTrain(plan);
		// 	return plan;
		// };
	}

	/*
	 * 스프링 배치에 대해 공부해 봐야하는 부분!!
	 * 나는 service에서 수정하고 반영하니까 writer에서 update를 해 줄 필요가 없다고 생각했음
	 * 그런데 writer로 반영을 해줘야 한다고 함.
	 * 사실 테스트를 이것 저것 해보고 싶지만 지금은 급한대로 사용하기..
	 */
	@Bean
	public ItemWriter<Plan> planPostponeWriter() {
		return new ItemWriter<Plan>() {
			@Override
			public void write(@NonNull Chunk<? extends Plan> plans) throws Exception {
				for (Plan plan : plans) {
					planService.updatePlan(plan);
				}
			}
		};

		// 람다식 표현
		// return plans -> plans.forEach(plan -> {
		// 	planService.updatePlan(plan);
		// });
	}
}
