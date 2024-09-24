package com.pacemaker.global.exception;

public class PlanAlreadyExistsException extends RuntimeException {

	public PlanAlreadyExistsException(String message) {
		super(message);
	}

	public PlanAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
