package com.pacemaker.global.exception.handler;

import java.util.Enumeration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.pacemaker.global.exception.ActivePlanNotFoundException;
import com.pacemaker.global.exception.ConflictException;
import com.pacemaker.global.exception.InvalidDateException;
import com.pacemaker.global.exception.NotFoundException;
import com.pacemaker.global.exception.PlanAlreadyExistsException;
import com.pacemaker.global.exception.PlanTrainEmptyException;
import com.pacemaker.global.exception.UserMismatchException;
import com.pacemaker.global.exception.WebClientTtsException;
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

	@ExceptionHandler(PlanAlreadyExistsException.class)
	public ResponseEntity<String> handlePlanAlreadyExistsException(PlanAlreadyExistsException e,
		HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(PlanTrainEmptyException.class)
	public ResponseEntity<String> handlePlanTrainEmptyException(PlanTrainEmptyException e, HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ActivePlanNotFoundException.class)
	public ResponseEntity<String> handleActivePlanNotFoundException(ActivePlanNotFoundException e,
		HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidDateException.class)
	public ResponseEntity<String> handleInvalidDateException(InvalidDateException e, HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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

	@ExceptionHandler(UserMismatchException.class)
	public ResponseEntity<String> handleUserMismatchException(UserMismatchException e, HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<String> handleNoResourceFoundExceptionException(NoResourceFoundException e,
		HttpServletRequest req) {
		// 계속 이상한 공격 들어와서 이거는 로그 안 찍게 하기

		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception e, HttpServletRequest req) {
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(),
			getParams(req)); // mattermost log 찍기

		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
	}

	@ExceptionHandler(WebClientTtsException.class)
	public ResponseEntity<String> handleWebClientTtsException(WebClientTtsException e, HttpServletRequest req) {
		// TTS 생성 오류 로그
		notificationManager.sendNotification(e, req.getRequestURI(), req.getMethod(), getParams(req));

		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
	}
}
