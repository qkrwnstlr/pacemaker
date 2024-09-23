package com.pacemaker.domain.openai.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.openai.service.OpenAiService;
import com.pacemaker.domain.plan.dto.ContentDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {
    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/test/mini")
    public Mono<ResponseEntity<String>> testMini(@RequestBody ContentDTO contentRequest) {

        System.out.println("Received message: " + contentRequest.message());
        System.out.println("Goal: " + contentRequest.context().goal());

        Instant start = Instant.now(); // 요청 시작 시간 기록

        return openAiService.testMini(contentRequest)
            .map(response -> {
                Instant finish = Instant.now(); // 응답 완료 시간 기록
                long timeElapsed = Duration.between(start, finish).toMillis(); // 시간 차이 계산
                System.out.println("Request processing time: " + timeElapsed + " milliseconds");
                return ResponseEntity.ok(response);
            });
    }

    @PostMapping("/test/4o")
    public Mono<ResponseEntity<String>> getTest4o(@RequestBody ContentDTO contentRequest) {

        System.out.println("Received message: " + contentRequest.message());
        System.out.println("Goal: " + contentRequest.context().goal());

        Instant start = Instant.now(); // 요청 시작 시간 기록

        return openAiService.test4o(contentRequest)
            .map(response -> {
                Instant finish = Instant.now(); // 응답 완료 시간 기록
                long timeElapsed = Duration.between(start, finish).toMillis(); // 시간 차이 계산
                System.out.println("Request processing time: " + timeElapsed + " milliseconds");
                return ResponseEntity.ok(response);
            });
    }
}
