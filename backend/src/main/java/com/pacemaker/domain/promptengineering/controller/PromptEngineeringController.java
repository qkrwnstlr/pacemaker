package com.pacemaker.domain.promptengineering.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.promptengineering.dto.PromptEngineeringRequest;
import com.pacemaker.domain.promptengineering.service.PromptEngineeringService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Tag(name = "PromptEngineering API", description = "PromptEngineering 기록하는 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/promptengineering")
public class PromptEngineeringController {

	private final PromptEngineeringService promptEngineeringService;

	@Operation(summary = "프롬프트 엔지니어링 로그 쌓기!")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "csv 작성 성공"),
		@ApiResponse(responseCode = "500", description = "csv 작성 중 오류 발생"),
	})
	@GetMapping("/chat")
	public Mono<?> chat(@Valid @RequestBody PromptEngineeringRequest request) {

		return promptEngineeringService.chat(request);
	}
}
