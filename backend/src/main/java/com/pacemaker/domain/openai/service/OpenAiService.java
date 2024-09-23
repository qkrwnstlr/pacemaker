package com.pacemaker.domain.openai.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.ChatCompletionResponse;
import com.pacemaker.domain.plan.dto.ContentRequest;
import com.pacemaker.domain.openai.dto.RealTimeResponseFormatString;
import com.pacemaker.domain.openai.dto.Message;
import com.pacemaker.domain.openai.dto.ResponseFormatString;
import com.pacemaker.domain.plan.dto.ContentResponse;
import com.pacemaker.domain.realtime.dto.RealTimeRequest;
import com.pacemaker.domain.realtime.dto.RealTimeResponse;

import reactor.core.publisher.Mono;

@Service
public class OpenAiService {

	private final WebClient openAIWebClient;

	public OpenAiService(WebClient openAIWebClient) {
		this.openAIWebClient = openAIWebClient;
	}

	public Mono<String> createPlanChatCompletions(ContentRequest contentRequest) {
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
				ContentResponse contentResponse = new Gson().fromJson(request.choices().get(0).message().content(),
					ContentResponse.class);
				System.out.println("contentResponse = " + contentResponse);

				// session 구하기
				calculateSession(contentResponse);

				// 날짜 변환
				// System.out.println(contentResponse.plan().planTrains().get(0).trainDate());
				// System.out.println("LocalDate: "+ LocalDate.parse(contentResponse.plan().planTrains().get(0).trainDate()));
				// System.out.println("LocalDateTime: "+ LocalDate.parse(contentResponse.plan().planTrains().get(0).trainDate()).atTime(0, 0));

				return new Gson().toJson(contentResponse);
			});
	}

	private void calculateSession(ContentResponse contentResponse) {
		List<ContentResponse.PlanTrain> planTrains = contentResponse.getPlan().getPlanTrains();

		if (planTrains != null && !planTrains.isEmpty()) {
			for (ContentResponse.PlanTrain planTrain : planTrains) {
				planTrain.calculateSession();
			}
		}
	}

	public Mono<String> testMini(ContentRequest contentRequest) {
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

	public Mono<String> test4o(ContentRequest contentRequest) {
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

	public Mono<String> createRealTimeChatCompletions(RealTimeRequest realTimeRequest) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createRealTimeSystem(), Message.createUser(new Gson().toJson(realTimeRequest)),
				Message.createRealTimeResponseFormat(RealTimeResponseFormatString.responseFormat.replaceAll("\\s+", ""))))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				ChatCompletionResponse request = new Gson().fromJson(response, ChatCompletionResponse.class);
				RealTimeResponse realTimeResponse = new Gson().fromJson(request.choices().get(0).message().content(),
					RealTimeResponse.class);
				System.out.println("realTimeResponse = " + realTimeResponse);

				return new Gson().toJson(realTimeResponse);
			});
	}
}
