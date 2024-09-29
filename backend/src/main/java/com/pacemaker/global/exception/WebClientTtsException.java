package com.pacemaker.global.exception;

public class WebClientTtsException extends RuntimeException {
	public WebClientTtsException(String message) {
		super(message);
	}

	public WebClientTtsException(String message, Throwable cause) {
		super(message, cause);
	}
}
