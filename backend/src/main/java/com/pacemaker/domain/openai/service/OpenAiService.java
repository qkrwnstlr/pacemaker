package com.pacemaker.domain.openai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.Message;

import reactor.core.publisher.Mono;

@Service
public class OpenAiService {

	private final WebClient openAIWebClient;

	public OpenAiService(WebClient openAIWebClient) {
		this.openAIWebClient = openAIWebClient;
	}

	public Mono<String> getCompletion(String content) {
		Message message = Message.builder()
			.role("user")
			.content(content)
			.build();

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-mini")
			.messages(List.of(message))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			// .uri("/completions") 이것은 GPT-3 모델
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class);
	}
}
