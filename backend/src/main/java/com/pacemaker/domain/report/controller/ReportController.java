package com.pacemaker.domain.report.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pacemaker.domain.openai.service.OpenAiService;
import com.pacemaker.domain.report.dto.CreateTrainEvaluationRequest;
import com.pacemaker.domain.report.dto.ReportFreeCreateRequest;
import com.pacemaker.domain.report.dto.ReportFreeResponse;
import com.pacemaker.domain.report.dto.ReportPlanCreateRequest;
import com.pacemaker.domain.report.dto.ReportPlanResponse;
import com.pacemaker.domain.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Tag(name = "Report API", description = "Report API 입니다.")
@RequestMapping("/reports")
@RestController
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;
	private final OpenAiService openAiService;

	@PostMapping("/plan")
	@Operation(summary = "레포트 생성 - 플랜 훈련")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "플랜 훈련 레포트 생성 성공"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "코치정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "훈련정보 조회 실패"),
		@ApiResponse(responseCode = "409", description = "동일한 훈련에 대한 레포트 생성 불가")
	})
	public ResponseEntity<ReportPlanResponse> createReportPlan(
		@RequestBody ReportPlanCreateRequest reportPlanCreateRequest) throws JsonProcessingException {
		return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReportPlan(reportPlanCreateRequest));
	}

	@GetMapping("/{id}/plan/user/{uid}")
	@Operation(summary = "레포트 조회 - 플랜 훈련")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "플랜 훈련 레포트 조회 성공"),
		@ApiResponse(responseCode = "400", description = "회원정보 불일치"),
		@ApiResponse(responseCode = "404", description = "레포트정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "레포트훈련정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "코치정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "훈련정보 조회 실패")
	})
	public ResponseEntity<ReportPlanResponse> findReportPlan(@PathVariable Long id, @PathVariable String uid) throws
		JsonProcessingException {
		return ResponseEntity.status(HttpStatus.OK).body(reportService.findReportPlan(id, uid));
	}

	@PostMapping("/free")
	@Operation(summary = "레포트 생성 - 내맘대로 달리기")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "내맘대로 달리기 레포트 생성 성공"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패")
	})
	public ResponseEntity<ReportFreeResponse> createFreeReport(
		@RequestBody ReportFreeCreateRequest reportFreeCreateRequest) throws JsonProcessingException {
		return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReportFree(reportFreeCreateRequest));
	}

	@GetMapping("{id}/free/user/{uid}")
	@Operation(summary = "레포트 조회 - 내맘대로 달리기")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "내맘대로 달리기 레포트 조회 성공"),
		@ApiResponse(responseCode = "400", description = "회원정보 불일치"),
		@ApiResponse(responseCode = "404", description = "레포트정보 조회 실패"),
		@ApiResponse(responseCode = "404", description = "회원정보 조회 실패")
	})
	public ResponseEntity<ReportFreeResponse> findReportFree(@PathVariable Long id, @PathVariable String uid) throws
		JsonProcessingException {
		return ResponseEntity.status(HttpStatus.OK).body(reportService.findReportFree(id, uid));
	}

	@PostMapping("/create/evaluation")
	@Operation(summary = "훈련 평가 Test API")
	public Mono<ResponseEntity<?>> createTrainEvaluation(@RequestBody CreateTrainEvaluationRequest createTrainEvaluationRequest) {

		Instant start = Instant.now();

		return openAiService.createTrainEvaluation(createTrainEvaluationRequest)
			.map(response -> {
				long timeElapsed = Duration.between(start, Instant.now()).toMillis();
				System.out.println("Request processing time: " + timeElapsed + " milliseconds"); // 나중에 log로 바꾸기

				return ResponseEntity.ok(response);
			});
	}
}
