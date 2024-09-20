package com.pacemaker.domain.openai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.Message;
import com.pacemaker.domain.openai.dto.ResponseFormatString;

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

	public Mono<String> getTestMini(String content) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-mini")
			.messages(List.of(Message.createSystem(), Message.createUser(content),
				// Message.createResponseFormat(ResponseFormatString.responseFormat)))
				Message.createResponseFormat(ResponseFormatString.responseFormat.replaceAll("\\s+", ""))))
			// .messages(List.of(Message.createSystem(), Message.createUser(content)))
			// .responseFormat(ResponseFormatString.responseFormat)
			// .responseFormat(new Gson().toJson(ResponseFormatString.responseFormat))
			// .responseFormat("{\"type\": \"json_object\"}")
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class);
	}

	public Mono<String> getTest4o(String content) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createSystem(), Message.createUser(content),
				Message.createResponseFormat(ResponseFormatString.responseFormat.replaceAll("\\s+", ""))))
			// .messages(List.of(Message.createSystem(), Message.createUser(content)))
			// .responseFormat(ResponseFormatString.responseFormat)
			// .responseFormat(new Gson().toJson(ResponseFormatString.responseFormat))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class);
	}
}
