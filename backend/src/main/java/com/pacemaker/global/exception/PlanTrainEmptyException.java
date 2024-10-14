package com.pacemaker.global.exception;

public class PlanTrainEmptyException extends RuntimeException {

	public PlanTrainEmptyException(String message) {
		super(message);
	}

	public PlanTrainEmptyException(String message, Throwable cause) {
		super(message, cause);
	}
}
