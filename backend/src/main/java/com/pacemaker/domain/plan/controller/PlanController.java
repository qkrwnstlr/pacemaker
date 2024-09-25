package com.pacemaker.domain.plan.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.openai.service.OpenAiService;
import com.pacemaker.domain.plan.dto.ContentRequest;
import com.pacemaker.domain.plan.dto.CreatePlanRequest;
import com.pacemaker.domain.plan.service.PlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

	private final PlanService planService;
	private final OpenAiService openAiService;

	@Operation(summary = "플랜 생성 채팅")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OpenAI API 호출 성공")
	})
	@PostMapping("/chat")
	public Mono<ResponseEntity<?>> createPlanChat(@RequestBody ContentRequest contentRequest) {
		
		Instant start = Instant.now();

		return openAiService.createPlanChatCompletions(contentRequest)
			.map(response -> {
				long timeElapsed = Duration.between(start, Instant.now()).toMillis();
				System.out.println("Request processing time: " + timeElapsed + " milliseconds"); // 나중에 log로 바꾸기

				return ResponseEntity.ok(response);
			});
	}

	@Operation(summary = "플랜 생성")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "플랜 생성 성공"),
		@ApiResponse(responseCode = "400", description = "훈련 리스트 존재하지 않음"),
		@ApiResponse(responseCode = "404", description = "해당 사용자 존재하지 않음"),
		@ApiResponse(responseCode = "409", description = "해당 사용자의 플랜이 이미 존재함")
	})
	@PostMapping("/create")
	public ResponseEntity<?> createPlan(@RequestBody CreatePlanRequest createPlanRequest) {

		Long id = planService.createPlan(createPlanRequest);

		System.out.println("최종 생성 Plan Id: " + id);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "현재 진행 중인 플랜 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "플랜 조회 성공"),
		@ApiResponse(responseCode = "404", description = "사용자의 진행 중인 플랜이 존재하지 않음")
	})
	@GetMapping("/active/user/{uid}")
	public ResponseEntity<?> getActivePlan(@PathVariable("uid") String uid) {

		return ResponseEntity.status(HttpStatus.OK).body(planService.findActivePlanByUid(uid));
	}

	@Operation(summary = "승준이를 위한 Active Plan 삭제 (status 변환이 아님 그냥 다 삭제임)")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "플랜 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "사용자의 진행 중인 플랜이 존재하지 않음")
	})
	@DeleteMapping("/active/user/{uid}")
	public ResponseEntity<?> deleteActivePlan(@PathVariable("uid") String uid) {
		planService.deleteActivePlanByUid(uid);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
