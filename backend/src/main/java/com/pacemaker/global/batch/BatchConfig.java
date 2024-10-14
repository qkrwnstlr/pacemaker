package com.pacemaker.global.batch;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.service.PlanService;
import com.pacemaker.global.batch.listener.CustomJobListener;
import com.pacemaker.global.batch.listener.CustomSkipListener;
import com.pacemaker.global.batch.listener.CustomStepListener;
import com.pacemaker.global.exception.PlanPostponeException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// @EnableBatchProcessing  //  배치 기능 활성 -> extends DefaultBatchConfiguration으로 대체 -> 순환 참조 발생하여 없앰
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final PlanService planService;

	@Bean
	public Job planPostponeJob() {
		return new JobBuilder("planPostponeJob", jobRepository)
			.listener(new CustomJobListener()) // Job 리스너 등록
			.start(planPostponeStep())
			.build();
	}

	@Bean
	public Step planPostponeStep() {
		return new StepBuilder("planPostponeStep", jobRepository)
			.<Plan, Plan>chunk(10, transactionManager)
			.reader(planPostponeReader())
			.processor(planPostponeProcessor())
			.writer(planPostponeWriter())
			.listener(new CustomJobListener()) // Job 리스너 등록
			.listener(new CustomStepListener()) // Step 리스너 등록
			.listener(new CustomSkipListener()) // Skip 리스너 등록
			.faultTolerant()
			.skip(PlanPostponeException.class)
			.skipLimit(10)
			.build();
	}

	@Bean
	public ItemReader<Plan> planPostponeReader() {
		return new ItemReader<Plan>() {
			List<Plan> plans = null;
			Iterator<Plan> planIterator = null;

			@Override
			public Plan read() {
				if (plans == null) {
					plans = planService.getPlansForPostpone();
					planIterator = plans.iterator();
				}

				if (planIterator.hasNext()) {
					// 다음 Plan이 있으면 반환
					return planIterator.next();
				} else {
					// 없으면 null을 반환 (plans = null 처리 안 해주면 getPlansForPostpone() 호출이 안됨)
					plans = null;
					return null;
				}
			}
		};
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
