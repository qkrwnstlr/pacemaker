package com.pacemaker.domain.openai.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pacemaker.domain.daily.dto.DailyCreateChatRequest;
import com.pacemaker.domain.daily.dto.DailyCreateChatResponse;
import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.ChatCompletionResponse;
import com.pacemaker.domain.openai.dto.Message;
import com.pacemaker.domain.openai.dto.ResponseFormatString;
import com.pacemaker.domain.plan.dto.ContentRequest;
import com.pacemaker.domain.plan.dto.ContentResponse;
import com.pacemaker.domain.realtime.dto.RealTimeFeedbackRequest;
import com.pacemaker.domain.realtime.dto.RealTimeFeedbackResponse;
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

		String planResponseFormatString = ResponseFormatString.planChatResponseFormat;

		JsonNode responseFormatNode;
		try {
			responseFormatNode = new ObjectMapper().readTree(planResponseFormatString);
		} catch (Exception e) {
			throw new RuntimeException("Response Format Json 생성 에러");
		}

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.maxTokens(8000)
			.messages(List.of(Message.createPlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(contentRequest))
				// ,Message.createPlanResponseFormat(ResponseFormatString.planChatResponseFormat.replaceAll("\\s+", ""))))
			))
			.responseFormat(responseFormatNode)
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

		String planResponseFormatString = ResponseFormatString.planChatResponseFormat;

		JsonNode responseFormatNode;
		try {
			responseFormatNode = new ObjectMapper().readTree(planResponseFormatString);
		} catch (Exception e) {
			throw new RuntimeException("Response Format Json 생성 에러");
		}

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-mini")
			.messages(List.of(Message.createPlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(contentRequest))))
			.responseFormat(responseFormatNode)
				// Message.createResponseFormat(ResponseFormatString.responseFormat)))
				// Message.createPlanResponseFormat(ResponseFormatString.planChatResponseFormat.replaceAll("\\s+", ""))))
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

		String planResponseFormatString = ResponseFormatString.planChatResponseFormat;

		JsonNode responseFormatNode;
		try {
			responseFormatNode = new ObjectMapper().readTree(planResponseFormatString);
		} catch (Exception e) {
			throw new RuntimeException("Response Format Json 생성 에러");
		}

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createPlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(contentRequest))))
				// Message.createPlanResponseFormat(ResponseFormatString.planChatResponseFormat.replaceAll("\\s+", ""))))
			// .messages(List.of(Message.createSystem(), Message.createUser(content)))
			// .responseFormat(ResponseFormatString.responseFormat)
			// .responseFormat(new Gson().toJson(ResponseFormatString.responseFormat))
			.responseFormat(responseFormatNode)
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class);
	}

	public Mono<String> createRealTimeChatCompletions(RealTimeFeedbackRequest realTimeFeedbackRequest) {

		String realTimeResponseFormatString = ResponseFormatString.realTimeResponseFormat;

		JsonNode responseFormatNode;
		try {
			responseFormatNode = new ObjectMapper().readTree(realTimeResponseFormatString);
		} catch (Exception e) {
			throw new RuntimeException("Response Format Json 생성 에러");
		}

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createRealTimeSystem(realTimeFeedbackRequest.coachTone()),
				Message.createUser(new Gson().toJson(realTimeFeedbackRequest))))
			.responseFormat(responseFormatNode)
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				ChatCompletionResponse request = new Gson().fromJson(response, ChatCompletionResponse.class);
				RealTimeFeedbackResponse realTimeFeedbackResponse = new Gson().fromJson(
					request.choices().getFirst().message().content(), RealTimeFeedbackResponse.class);
				System.out.println("realTimeFeedbackResponse = " + realTimeFeedbackResponse);

				return realTimeFeedbackResponse.textFeedback() + " " + realTimeFeedbackResponse.textCheer();
			});
	}

	public Mono<String> createDailyCreateChat(DailyCreateChatRequest dailyCreateChatRequest) {

		String dialyResponseFormatString = ResponseFormatString.dailyCreateChatResponseFormat;

		JsonNode responseFormatNode;
		try {
			responseFormatNode = new ObjectMapper().readTree(dialyResponseFormatString);
		} catch (Exception e) {
			throw new RuntimeException("Response Format Json 생성 에러");
		}

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createDailySystem(dailyCreateChatRequest.coachTone()),
				Message.createUser(new Gson().toJson(dailyCreateChatRequest))))
			.responseFormat(responseFormatNode)
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

		String evaluationResponseFormatString = ResponseFormatString.createTrainEvaluationResponseFormat;

		JsonNode responseFormatNode;
		try {
			responseFormatNode = new ObjectMapper().readTree(evaluationResponseFormatString);
		} catch (Exception e) {
			throw new RuntimeException("Response Format Json 생성 에러");
		}

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createTrainEvaluation(createTrainEvaluationRequest.coachTone()),
				Message.createUser(new Gson().toJson(createTrainEvaluationRequest))))
			.responseFormat(responseFormatNode)
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

	public Mono<String> updatePlanChatCompletions(ContentRequest contentRequest) {
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.maxTokens(8000)
			.messages(List.of(Message.updatePlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(contentRequest)),
				Message.updatePlanResponseFormat(ResponseFormatString.planChatResponseFormat.replaceAll("\\s+", ""))))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				ChatCompletionResponse request = new Gson().fromJson(response, ChatCompletionResponse.class);
				ContentResponse contentResponse = new Gson().fromJson(request.choices().getFirst().message().content(),
					ContentResponse.class);

				calculateSession(contentResponse);

				return new Gson().toJson(contentResponse);
			});
	}
}
