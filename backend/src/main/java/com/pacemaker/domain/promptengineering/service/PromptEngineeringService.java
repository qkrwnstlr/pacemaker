package com.pacemaker.domain.promptengineering.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.openai.dto.ChatCompletionResponse;
import com.pacemaker.domain.openai.dto.Message;
import com.pacemaker.domain.plan.dto.ContentResponse;
import com.pacemaker.domain.promptengineering.dto.PromptEngineeringRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PromptEngineeringService {

	private final WebClient openAIWebClient;

	private final String csvFilePath = "/promptengineering";
	private final String csvColumns[] = {"System Message", "Response Format", "Content Request", "Content Response"};

	public Mono<String> chat(PromptEngineeringRequest request) {

		// OpenAI API로 요청을 보낼 request 생성하기
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(List.of(Message.createPlanResponseFormat(request.getResponseFormat()),
				Message.createUser(new Gson().toJson(request.getContentRequest())),
				Message.createPlanResponseFormat(request.getResponseFormat().replaceAll("\\s+", ""))))
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {

				ChatCompletionResponse chatCompletionResponse = new Gson().fromJson(response,
					ChatCompletionResponse.class);

				ContentResponse contentResponse = new Gson().fromJson(
					chatCompletionResponse.choices().getFirst().message().content(),
					ContentResponse.class);

				// session 구하기
				calculateSession(contentResponse);

				// csv 파일 생성
				writeCSV(request, contentResponse);

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

	private void writeCSV(PromptEngineeringRequest request, ContentResponse contentResponse) {

		String filePath = csvFilePath + "/" + request.getName() + "/" + request.getFilename();

		// try-catch-resource로도 할 수 있으나 직관적으로 보이기 위해 이렇게 함
		CSVWriter writer = null;
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;

		String reqStr = null;
		String resStr = null;

		try {
			fos = new FileOutputStream(filePath, true); // true: append mode
			osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			writer = new CSVWriter(osw);

			// 파일이 비어 있는 경우 컬럼명 작성
			if (new File(filePath).length() == 0) {
				writer.writeNext(csvColumns);
			}

			reqStr = new Gson().toJson(request.getContentRequest());
			resStr = new Gson().toJson(contentResponse);

			String[] record = {request.getSystemMessage(), request.getResponseFormat(),
				reqStr == null ? "null" : reqStr, resStr == null ? "null" : resStr};
			writer.writeNext(record);

		} catch (IOException e) {
			// 예외 처리 추가
			throw new RuntimeException("CSV 파일 작성 중 오류가 발생했습니다: " + e.getMessage(), e);

		} finally {
			// 자원 해제
			try {
				if (writer != null) {
					writer.close();
				}

				if (osw != null) {
					osw.close();
				}

				if (fos != null) {
					fos.close();
				}

			} catch (IOException e) {
				throw new RuntimeException("자원 해제 중 오류가 발생했습니다: " + e.getMessage(), e);
			}
		}
	}
}
