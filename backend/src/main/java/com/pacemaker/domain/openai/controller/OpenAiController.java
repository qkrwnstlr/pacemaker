package com.pacemaker.domain.openai.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pacemaker.domain.openai.service.OpenAiService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {
    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/gpt-4o-mini")
    public Mono<ResponseEntity<String>> getGpt4oMini(@RequestBody String content) {

        Instant start = Instant.now(); // 요청 시작 시간 기록

        if (content == null || content.isEmpty()) {
            return Mono.just(new ResponseEntity<>("에헤이 잘못된 요청!", HttpStatus.BAD_REQUEST));
        }

        // 아래 주석 부분은 비동기적 특성을 잃어버림!!
//        Mono<String> response = openAiService.getCompletion(completionRequestDTO);
//
//        Instant finish = Instant.now(); // 응답 완료 시간 기록
//        long timeElapsed = Duration.between(start, finish).toMillis(); // 시간 차이 계산
//        System.out.println("Request processing time: " + timeElapsed + " milliseconds");
//
//        return Mono.just(new ResponseEntity<>(response.block(), HttpStatus.OK));

        return openAiService.getCompletion(content)
                .map(response -> {
                    Instant finish = Instant.now(); // 응답 완료 시간 기록
                    long timeElapsed = Duration.between(start, finish).toMillis(); // 시간 차이 계산
                    System.out.println("Request processing time: " + timeElapsed + " milliseconds");
                    return ResponseEntity.ok(response);
                });
    }
}
