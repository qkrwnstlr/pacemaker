package com.pacemaker.domain.plan.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
		@ApiResponse(responseCode = "201", description = "플랜 생성 성공")
	})
	@PostMapping("/create")
	public ResponseEntity<?> createPlanCreate(@RequestBody CreatePlanRequest createPlanRequest) {

		planService.createPlan(createPlanRequest);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
