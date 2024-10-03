package com.pacemaker.domain.openai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pacemaker.domain.daily.dto.DailyCreateChatRequest;
import com.pacemaker.domain.daily.dto.DailyCreateChatResponse;
import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.ChatCompletionResponse;
import com.pacemaker.domain.openai.dto.LlmContent;
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

		// 직렬화 -> 역직렬화로 llmContent에 meessage, context 필드 채우기
		LlmContent llmRequestContent = new Gson().fromJson(new Gson().toJson(contentRequest), LlmContent.class);

		// contentRequest -> llmContent 변경
		LlmContent convertedLlmContent = convertContentRequestToLlmContent(contentRequest, llmRequestContent);

		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.maxTokens(8000)
			.messages(List.of(Message.createPlanEngSystem(contentRequest.coachTone()),
				Message.createUser(new Gson().toJson(convertedLlmContent))
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
				LlmContent llmContent = new Gson().fromJson(request.choices().getFirst().message().content(),
					LlmContent.class);
				System.out.println("contentResponse = " + contentResponse);
				System.out.println("llmContentResponse = " + llmContent);

				ContentResponse convertedContentResponse = convertLlmResponseToContentResponse(contentResponse,
					llmContent);
				System.out.println("convertedContentResponse = " + convertedContentResponse);

				// session 구하기
				// calculateSession(contentResponse);

				// 날짜 변환
				// System.out.println(contentResponse.plan().planTrains().get(0).trainDate());
				// System.out.println("LocalDate: "+ LocalDate.parse(contentResponse.plan().planTrains().get(0).trainDate()));
				// System.out.println("LocalDateTime: "+ LocalDate.parse(contentResponse.plan().planTrains().get(0).trainDate()).atTime(0, 0));

				return new Gson().toJson(convertedContentResponse);
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

	public ContentResponse convertLlmResponseToContentResponse(ContentResponse contentResponse, LlmContent llmContent) {

		System.out.println("llmContentResponse = " + new Gson().toJson(llmContent));

		ContentResponse convertedContentResponse = new ContentResponse();
		// content, message 복사
		convertedContentResponse.setMessage(contentResponse.getMessage());
		convertedContentResponse.setContext(contentResponse.getContext());

		// Plan 변환
		LlmContent.Plan llmPlan = llmContent.getPlan();
		ContentResponse.Plan convertedPlan = new ContentResponse.Plan();

		List<Integer> indices = llmPlan.getIndex();
		List<String> trainDates = llmPlan.getTrainDate();
		List<String> paramTypes = llmPlan.getParamType();
		List<Integer> repetitions = llmPlan.getRepetition();
		List<Integer> trainParams = llmPlan.getTrainParam();
		List<Integer> trainPaces = llmPlan.getTrainPace();
		List<Integer> interParams = llmPlan.getInterParam();

		List<ContentResponse.PlanTrain> planTrains = new ArrayList<>();

		// 간혹 배열의 크기가 잘려서 오는 경우가 있어서, 에러 방지를 위한 최소값 구하기
		int minSize = Stream.of(indices.size(), trainDates.size(), paramTypes.size(),
				repetitions.size(), trainParams.size(), trainPaces.size(), interParams.size())
			.min(Integer::compare)
			.orElse(0);

		for (int i = 0; i < minSize; i++) {
			ContentResponse.PlanTrain planTrain = new ContentResponse.PlanTrain();
			planTrain.setIndex(indices.get(i));
			planTrain.setTrainDate(trainDates.get(i));
			planTrain.setParamType(paramTypes.get(i));
			planTrain.setRepetition(repetitions.get(i));
			planTrain.setTrainParam(trainParams.get(i));
			planTrain.setTrainPace(trainPaces.get(i));
			planTrain.setInterParam(interParams.get(i));

			// 세션 계산
			planTrain.calculateSession();

			planTrains.add(planTrain);
		}

		convertedPlan.setPlanTrains(planTrains);

		convertedContentResponse.setPlan(convertedPlan);

		return convertedContentResponse;
	}

	public LlmContent convertContentRequestToLlmContent(ContentRequest contentRequest, LlmContent llmContent) {

		LlmContent convertedLlmContent = new LlmContent();

		// context, message 복사
		convertedLlmContent.setMessage(llmContent.getMessage());
		convertedLlmContent.setContext(llmContent.getContext());

		// Plan 변환
		ContentRequest.Plan contentPlan = contentRequest.plan();
		LlmContent.Plan llmPlan = llmContent.new Plan();

		if (contentPlan.planTrains() == null) {
			return convertedLlmContent;
		}

		List<Integer> indices = contentPlan.planTrains().stream().map(ContentRequest.Plan.PlanTrain::index).toList();
		List<String> trainDates = contentPlan.planTrains()
			.stream()
			.map(ContentRequest.Plan.PlanTrain::trainDate)
			.toList();
		List<String> paramTypes = contentPlan.planTrains()
			.stream()
			.map(ContentRequest.Plan.PlanTrain::paramType)
			.toList();
		List<Integer> repetitions = contentPlan.planTrains()
			.stream()
			.map(ContentRequest.Plan.PlanTrain::repetition)
			.toList();
		List<Integer> trainParams = contentPlan.planTrains()
			.stream()
			.map(ContentRequest.Plan.PlanTrain::trainParam)
			.toList();
		List<Integer> trainPaces = contentPlan.planTrains()
			.stream()
			.map(ContentRequest.Plan.PlanTrain::trainPace)
			.toList();
		List<Integer> interParams = contentPlan.planTrains()
			.stream()
			.map(ContentRequest.Plan.PlanTrain::interParam)
			.toList();

		llmPlan.setIndex(indices);
		llmPlan.setTrainDate(trainDates);
		llmPlan.setParamType(paramTypes);
		llmPlan.setRepetition(repetitions);
		llmPlan.setTrainParam(trainParams);
		llmPlan.setTrainPace(trainPaces);
		llmPlan.setInterParam(interParams);

		convertedLlmContent.setPlan(llmPlan);

		return convertedLlmContent;
	}
}
