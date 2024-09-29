package com.pacemaker.domain.realtime.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.openai.service.OpenAiService;
import com.pacemaker.domain.realtime.dto.RealTimeFeedbackRequest;
import com.pacemaker.domain.realtime.service.RealTimeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/realtimes")
public class RealTimeController {

	private final OpenAiService openAiService;
	private final RealTimeService realTimeService;

	@Operation(summary = "실시간 피드백 음성")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "실시간 피드백 코치 음성 생성 성공")
	})
	@PostMapping("/feedback")
	public Mono<ResponseEntity<?>> createRealTimeFeedback(@RequestBody RealTimeFeedbackRequest realTimeFeedbackRequest) {
		Instant start = Instant.now();

		return openAiService.createRealTimeChatCompletions(realTimeFeedbackRequest)
			.flatMap(response -> {
				long timeElapsed = Duration.between(start, Instant.now()).toMillis();
				System.out.println("GPT processing time: " + timeElapsed + " milliseconds"); // 나중에 log로 바꾸기

				return realTimeService.createRealTimeTts(response, realTimeFeedbackRequest.coachIndex());
			})
			.map(wavFile -> {
				long timeElapsed = Duration.between(start, Instant.now()).toMillis();
				System.out.println("Total processing time: " + timeElapsed + " milliseconds");

				return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, "audio/wav")
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"feedback.wav\"")
					.body(wavFile);
			});
	}
}
