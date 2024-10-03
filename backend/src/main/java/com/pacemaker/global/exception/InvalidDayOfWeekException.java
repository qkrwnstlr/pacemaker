package com.pacemaker.global.exception;

public class InvalidDayOfWeekException extends RuntimeException {

	public InvalidDayOfWeekException(String message) {
		super(message);
	}

	public InvalidDayOfWeekException(String message, Throwable cause) {
		super(message, cause);
	}
}
