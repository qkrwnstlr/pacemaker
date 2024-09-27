package com.pacemaker.domain.realtime.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class RealTimeService {

	private final WebClient gpuServerWebClient;

	public RealTimeService(WebClient gpuServerWebClient) {
		this.gpuServerWebClient = gpuServerWebClient;
	}

	public Mono<byte[]> createRealTimeTts(String message, int coachIndex) {
		return gpuServerWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/tts")
				.queryParam("message", message)
				.queryParam("coach_index", coachIndex)
				.build())
			.retrieve()
			.bodyToMono(byte[].class);
	}
}
