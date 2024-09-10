package com.pacemaker.domain.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.report.dto.ReportFreeRequest;
import com.pacemaker.domain.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Report API", description = "Report API 입니다.")
@RequestMapping("/reports")
@RestController
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping("/free")
	@Tag(name = "Report API")
	@Operation(
		summary = "내맘대로 달리기 레포트 생성"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "내맘대로 달리기 레포트 생성 성공")
	})
	public ResponseEntity<?> createFreeReport(@Valid @RequestBody
	ReportFreeRequest reportFreeRequest) {
		reportService.createFree(reportFreeRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
