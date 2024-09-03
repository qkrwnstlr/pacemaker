package com.pacemaker.global.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pacemaker.global.exception.InvalidUsernameException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidUsernameException.class)
	public ResponseEntity<String> handleInvalidUsernameException(InvalidUsernameException e) {
		String jsonResponse = "{\"message\": \"" + e.getMessage() + "\", \"errorCode\": \"4000\"}";
		return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST); // 400
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
	}

}
