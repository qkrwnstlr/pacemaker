package com.pacemaker.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.user.dto.CheckUidResponse;
import com.pacemaker.domain.user.dto.UserCreateRequest;
import com.pacemaker.domain.user.dto.UserRequest;
import com.pacemaker.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User API", description = "User API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

	private final UserService userService;

	@PostMapping
	@Operation(summary = "회원가입")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공")
	})
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
		userService.create(userCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/checkuid")
	@Operation(summary = "uid 중복체크")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "UID 중복체크 완료")
	})
	public ResponseEntity<CheckUidResponse> checkUid(@RequestBody UserRequest userRequest) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.checkUid(userRequest));
	}
}
