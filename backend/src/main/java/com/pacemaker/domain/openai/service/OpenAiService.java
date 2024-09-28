package com.pacemaker.domain.openai.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.pacemaker.domain.daily.dto.DailyCreateChatRequest;
import com.pacemaker.domain.daily.dto.DailyCreateChatResponse;
import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.ChatCompletionResponse;
import com.pacemaker.domain.openai.dto.Message;
import com.pacemaker.domain.openai.dto.ResponseFormatString;
import com.pacemaker.domain.plan.dto.ContentRequest;
import com.pacemaker.domain.plan.dto.ContentResponse;
import com.pacemaker.domain.realtime.dto.RealTimeRequest;
import com.pacemaker.domain.realtime.dto.RealTimeResponse;
import com.pacemaker.domain.report.dto.CreateTrainEvaluationRequest;
import com.pacemaker.domain.report.dto.CreateTrainEvaluationResponse;

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
			.messages(List.of(Message.createPlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(contentRequest)),
				Message.createPlanResponseFormat(ResponseFormatString.planChatResponseFormat.replaceAll("\\s+", ""))))
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
				ContentResponse contentResponse = new Gson().fromJson(request.choices().getFirst().message().content(),
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
		if (contentResponse.getPlan() == null) {
			// 나중에 다른 서비스 처리 로직이 정해지면 Exception으로 처리해서 변경하든 하기!
			return;
		}

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
			.messages(List.of(Message.createPlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(contentRequest)),
				// Message.createResponseFormat(ResponseFormatString.responseFormat)))
				Message.createPlanResponseFormat(ResponseFormatString.planChatResponseFormat.replaceAll("\\s+", ""))))
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
			.messages(List.of(Message.createPlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(contentRequest)),
				Message.createPlanResponseFormat(ResponseFormatString.planChatResponseFormat.replaceAll("\\s+", ""))))
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
			.messages(List.of(Message.createRealTimeSystem(realTimeRequest.coachTone()),
				Message.createUser(new Gson().toJson(realTimeRequest)), Message.createRealTimeResponseFormat(
					ResponseFormatString.realTimeResponseFormat.replaceAll("\\s+", ""))))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				ChatCompletionResponse request = new Gson().fromJson(response, ChatCompletionResponse.class);
				RealTimeResponse realTimeResponse = new Gson().fromJson(
					request.choices().getFirst().message().content(), RealTimeResponse.class);
				System.out.println("realTimeResponse = " + realTimeResponse);

				return realTimeResponse.textFeedback() + " " + realTimeResponse.textCheer();
			});
	}

	public Mono<String> createDailyCreateChat(DailyCreateChatRequest dailyCreateChatRequest) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createDailySystem(dailyCreateChatRequest.coachTone()),
				Message.createUser(new Gson().toJson(dailyCreateChatRequest)), Message.createDailyResponseFormat(
					ResponseFormatString.dailyCreateChatResponseFormat.replaceAll("\\s+", ""))))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				ChatCompletionResponse request = new Gson().fromJson(response, ChatCompletionResponse.class);
				DailyCreateChatResponse dailyCreateChatResponse = new Gson().fromJson(
					request.choices().getFirst().message().content(), DailyCreateChatResponse.class);

				return new Gson().toJson(dailyCreateChatResponse);
			});
	}

	public Mono<String> createTrainEvaluation(CreateTrainEvaluationRequest createTrainEvaluationRequest) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createTrainEvaluation(createTrainEvaluationRequest.coachTone()),
				Message.createUser(new Gson().toJson(createTrainEvaluationRequest)),
				Message.createTrainEvaluationResponseFormat(
					ResponseFormatString.createTrainEvaluationResponseFormat.replaceAll("\\s+", ""))))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				ChatCompletionResponse request = new Gson().fromJson(response, ChatCompletionResponse.class);
				CreateTrainEvaluationResponse createTrainEvaluationResponse = new Gson().fromJson(
					request.choices().getFirst().message().content(), CreateTrainEvaluationResponse.class);
				return new Gson().toJson(createTrainEvaluationResponse);
			});
	}
}
