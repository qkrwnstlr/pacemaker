package com.pacemaker.global.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CustomStepListener implements StepExecutionListener {
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// 현재 청크 인덱스 출력
		System.out.println("Current Chunk Index: " + stepExecution.getReadCount());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// 청크 처리 후
		return stepExecution.getExitStatus();
	}
}

