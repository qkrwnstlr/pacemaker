package com.pacemaker.domain.promptengineering.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.promptengineering.service.PromptEngineeringService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Tag(name = "PromptEngineering API", description = "PromptEngineering 기록하는 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/promptengineering")
public class PromptEngineeringController {

	private final PromptEngineeringService promptEngineeringService;
	
	@GetMapping("/test")
	public Mono<?> test() {

		// return promptEngineeringService.test();
		return null;
	}
}
