package com.pacemaker.domain.realtime.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pacemaker.global.exception.WebClientTtsException;

import reactor.core.publisher.Mono;

@Service
public class RealTimeService {

	private final WebClient gpuServerWebClient;

	public RealTimeService(WebClient gpuServerWebClient) {
		this.gpuServerWebClient = gpuServerWebClient;
	}

	public Mono<byte[]> createRealTimeTts(String message, Long coachNumber) {
		return gpuServerWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/tts")
				.queryParam("message", message)
				.queryParam("coachNumber", coachNumber)
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
				// GPU 서버에서 500번 응답을 받으면 WebClientTtsException 발생
				return  Mono.error(new WebClientTtsException("GPU 서버에서 음성 생성 실패"));
			})
			.bodyToMono(byte[].class)
			.onErrorMap(e -> new WebClientTtsException("GPU 서버 통신 오류로 음성 생성 실패", e));
	}
}
