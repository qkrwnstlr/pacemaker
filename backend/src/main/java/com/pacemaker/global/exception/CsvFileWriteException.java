package com.pacemaker.global.exception;

public class CsvFileWriteException extends RuntimeException {

	public CsvFileWriteException(String message) {
		super(message);
	}

	public CsvFileWriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
