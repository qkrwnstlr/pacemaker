package com.pacemaker.domain.realtime.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.openai.service.OpenAiService;
import com.pacemaker.domain.realtime.dto.RealTimeRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/realtimes")
public class RealTimeController {

	private final OpenAiService openAiService;

	@PostMapping
	public Mono<ResponseEntity<?>> createRealTimeFeedback(@RequestBody RealTimeRequest realTimeRequest) {
		Instant start = Instant.now();

		return openAiService.createRealTimeChatCompletions(realTimeRequest)
			.map(response -> {
				long timeElapsed = Duration.between(start, Instant.now()).toMillis();
				System.out.println("Request processing time: " + timeElapsed + " milliseconds"); // 나중에 log로 바꾸기

				return ResponseEntity.ok(response);
			});
	};
}
