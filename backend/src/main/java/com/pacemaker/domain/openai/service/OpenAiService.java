package com.pacemaker.domain.openai.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.ChatCompletionResponse;
import com.pacemaker.domain.plan.dto.ContentDTO;
import com.pacemaker.domain.openai.dto.Message;
import com.pacemaker.domain.openai.dto.ResponseFormatString;

import reactor.core.publisher.Mono;

@Service
public class OpenAiService {

	private final WebClient openAIWebClient;

	public OpenAiService(WebClient openAIWebClient) {
		this.openAIWebClient = openAIWebClient;
	}

	public Mono<String> createPlanChatCompletions(ContentDTO contentRequest) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createPlanEngSystem(), Message.createUser(new Gson().toJson(contentRequest)),
				Message.createPlanResponseFormat(ResponseFormatString.responseFormat.replaceAll("\\s+", ""))))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				// int startIdx = KMP.getStartIndex("\\\"context\\\":", response);
				// System.out.println("responseLen: " + response.length());
				// System.out.println("startIdx = " + startIdx);
				// return response + " | startIdx: " + startIdx;

				ChatCompletionResponse request = new Gson().fromJson(response, ChatCompletionResponse.class);
				// System.out.println(request.choices().get(0));
				// System.out.println(request.choices().get(0).index());
				// System.out.println(request.choices().get(0).message());
				// System.out.println(request.choices.get(0).message.content.toString());
				ContentDTO contentResponse = new Gson().fromJson(request.choices().get(0).message().content(),
					ContentDTO.class);
				System.out.println("contentResponse = " + contentResponse);

				return new Gson().toJson(contentResponse);
			});
	}

	public Mono<String> testMini(ContentDTO contentRequest) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-mini")
			.messages(List.of(Message.createPlanEngSystem(), Message.createUser(new Gson().toJson(contentRequest)),
				// Message.createResponseFormat(ResponseFormatString.responseFormat)))
				Message.createPlanResponseFormat(ResponseFormatString.responseFormat.replaceAll("\\s+", ""))))
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

	public Mono<String> test4o(ContentDTO contentRequest) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createPlanEngSystem(), Message.createUser(new Gson().toJson(contentRequest)),
				Message.createPlanResponseFormat(ResponseFormatString.responseFormat.replaceAll("\\s+", ""))))
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
