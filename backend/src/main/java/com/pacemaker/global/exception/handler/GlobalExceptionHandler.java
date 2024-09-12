package com.pacemaker.global.exception.handler;

import java.util.Enumeration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pacemaker.global.exception.ConflictException;
import com.pacemaker.global.exception.InvalidUsernameException;
import com.pacemaker.global.exception.NotFoundException;
import com.pacemaker.global.util.mattermost.NotificationManager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final NotificationManager notificationManager;

	private String getParams(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();

		Enumeration<String> keys = req.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			sb.append("- ").append(key).append(" : ").append(req.getParameter(key)).append('\n');
		}

		return sb.toString();
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFoundException(NotFoundException e, HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<String> handleConflictException(ConflictException e, HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception e, HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req)); // mattermost log 찍기

		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
	}
}
