package com.pacemaker.global.exception;

public class ActivePlanNotFoundException extends RuntimeException {

	public ActivePlanNotFoundException(String message) {
		super(message);
	}

	public ActivePlanNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
