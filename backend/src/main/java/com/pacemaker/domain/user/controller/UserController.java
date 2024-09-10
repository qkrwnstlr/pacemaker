package com.pacemaker.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.coach.dto.CoachUpdateRequest;
import com.pacemaker.domain.user.dto.CheckUidResponse;
import com.pacemaker.domain.coach.dto.CoachNumberResponse;
import com.pacemaker.domain.user.dto.UserCreateRequest;
import com.pacemaker.domain.user.dto.UserInfoResponse;
import com.pacemaker.domain.user.dto.UserRequest;
import com.pacemaker.domain.user.dto.UserUpdateRequest;
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

	@GetMapping("/{uid}")
	@Operation(summary = "회원정보 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원정보 확인 성공")
	})
	public ResponseEntity<UserInfoResponse> findUser(@PathVariable String uid) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(uid));
	}

	@PutMapping
	@Operation(summary = "회원정보 수정")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원정보 수정 성공")
	})
	public ResponseEntity<UserInfoResponse> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserInfo(userUpdateRequest));
	}

	@GetMapping("/{uid}/coach")
	@Operation(summary = "내 코치 정보 요청")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "코치 확인 성공")
	})
	public ResponseEntity<CoachNumberResponse> findCoachNumber(@PathVariable String uid) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getCoachNumber(uid));
	}

	@PutMapping("/{uid}/coach")
	@Operation(summary = "내 코치 정보 수정")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "내 코치 정보 수정 완료")
	})
	public ResponseEntity<?> updateCoach(@PathVariable String uid, @RequestBody CoachUpdateRequest coachUpdateRequest) {
		userService.updateCoachNumber(uid, coachUpdateRequest);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping
	@Operation(summary = "회원탈퇴")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "회원 탈퇴 완료")
	})
	public ResponseEntity<?> deleteUser(@RequestBody UserRequest userRequest) {
		userService.deleteUser(userRequest);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
