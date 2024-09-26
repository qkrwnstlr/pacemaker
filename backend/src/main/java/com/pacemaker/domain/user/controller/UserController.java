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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.coach.dto.CoachNumberResponse;
import com.pacemaker.domain.coach.dto.CoachUpdateRequest;
import com.pacemaker.domain.user.dto.GoogleLoginRequest;
import com.pacemaker.domain.user.dto.GoogleLoginResponse;
import com.pacemaker.domain.user.dto.UserInfoResponse;
import com.pacemaker.domain.user.dto.UserUpdateRequest;
import com.pacemaker.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User API", description = "User API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/users/{uid}")
@RestController
public class UserController {

	private final UserService userService;

	@PostMapping
	@Operation(summary = "구글 로그인")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "구글 로그인 성공")
	})
	public ResponseEntity<GoogleLoginResponse> googleLogin(@PathVariable String uid,
		@RequestBody GoogleLoginRequest googleLoginRequest) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.googleLogin(uid, googleLoginRequest));
	}

	@GetMapping
	@Operation(summary = "회원정보 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원정보 조회 성공"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패")
	})
	public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable String uid) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(uid));
	}

	@PutMapping
	@Operation(summary = "회원정보 수정")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원정보 수정 성공"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패")
	})
	public ResponseEntity<UserInfoResponse> updateUser(@PathVariable String uid,
		@RequestBody UserUpdateRequest userUpdateRequest) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserInfo(uid, userUpdateRequest));
	}

	@GetMapping("/coach")
	@Operation(summary = "내 코치 정보 요청")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "코치 확인 성공"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패")
	})
	public ResponseEntity<CoachNumberResponse> findCoachNumber(@PathVariable String uid) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getCoachNumber(uid));
	}

	@PutMapping("/coach")
	@Operation(summary = "내 코치 정보 수정")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "내 코치 정보 수정 완료"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "코치정보 조회 실패")
	})
	public ResponseEntity<?> updateCoach(@PathVariable String uid, @RequestBody CoachUpdateRequest coachUpdateRequest) {
		userService.updateCoachNumber(uid, coachUpdateRequest);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@DeleteMapping
	@Operation(summary = "회원탈퇴")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "회원 탈퇴 완료")
	})
	public ResponseEntity<?> deleteUser(@PathVariable String uid) {
		userService.deleteUser(uid);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

	@Operation(summary = "월별 캘린더 조회 (존재하지 않는 사용자는 따로 404 안 터트림 -> null)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "월별 캘린더 조회 성공"),
	})
	@GetMapping("/calender")
	public ResponseEntity<?> getMonthlyCalender(@PathVariable("uid") String uid, @RequestParam Integer year,
		@RequestParam Integer month) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.findMonthlyCalenderByUid(uid, year, month));
	}
}
