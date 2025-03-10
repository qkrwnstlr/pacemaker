package com.pacemaker.domain.daily.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.daily.dto.DailyCreateChatRequest;
import com.pacemaker.domain.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
public class DailyController {

	private final OpenAiService openAiService;

	@PostMapping("/chat")
	public Mono<ResponseEntity<?>> createDailyChat(@RequestBody DailyCreateChatRequest dailyCreateChatRequest) {
		Instant start = Instant.now();

		return openAiService.createDailyCreateChat(dailyCreateChatRequest)
			.map(response -> {
				long timeElapsed = Duration.between(start, Instant.now()).toMillis();
				System.out.println("Request processing time: " + timeElapsed + " milliseconds");

				return ResponseEntity.ok(response);
			});
	}
}
