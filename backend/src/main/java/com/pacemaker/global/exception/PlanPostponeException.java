package com.pacemaker.global.exception;

public class PlanPostponeException extends RuntimeException {

	public PlanPostponeException(String message) {
		super(message);
	}

	public PlanPostponeException(String message, Throwable cause) {
		super(message, cause);
	}
}
