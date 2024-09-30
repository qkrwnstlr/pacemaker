package com.pacemaker.domain.promptengineering.service;

import java.io.File;
import java.io.FileOutputStream;
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
import com.pacemaker.domain.openai.dto.ResponseFormatString;
import com.pacemaker.domain.plan.dto.ContentResponse;
import com.pacemaker.domain.promptengineering.dto.PromptEngineeringRequest;
import com.pacemaker.domain.promptengineering.dto.UsageResponse;
import com.pacemaker.global.exception.CsvFileWriteException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PromptEngineeringService {

	private final WebClient openAIWebClient;

	private final String csvFilePath = "promptengineering";
	private final String csvColumns[] = {"System Message", "Response Format", "Content Request", "Content Response",
		"Usage", "Response Format Mismatch"};

	public Mono<String> chat(PromptEngineeringRequest request) {

		Message responseFormat = Message.createPlanResponseFormat((request.getResponseFormat() == null ?
			ResponseFormatString.planChatResponseFormat :
			request.getResponseFormat()).replaceAll("\\s", ""));

		Message system = request.getSystemMessage() == null ?
			Message.createPlanEngSystem(request.getContentRequest().coachTone()) :
			Message.createSystem(request.getSystemMessage());

		// OpenAI API로 요청을 보낼 request 생성하기
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
			.model("gpt-4o-2024-08-06")
			.messages(
				List.of(responseFormat, Message.createUser(new Gson().toJson(request.getContentRequest())), system))
			.temperature(request.getChatCompletionRequest().temperature())
			.maxTokens(request.getChatCompletionRequest().maxTokens())
			.build();

		return openAIWebClient.post()
			.uri("/chat/completions")
			.bodyValue(chatCompletionRequest)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {

				ChatCompletionResponse chatCompletionResponse = new Gson().fromJson(response,
					ChatCompletionResponse.class);

				ContentResponse contentResponse = null;
				boolean responseFormatMismatch = false;
				try {
					contentResponse = new Gson().fromJson(
						chatCompletionResponse.choices().getFirst().message().content(),
						ContentResponse.class);
				} catch (Exception e) {
					System.out.println("response format에 어긋난 try-catch");
					responseFormatMismatch = true;
					contentResponse = new Gson().fromJson("{\"message\":\"%s\"}".formatted(
						chatCompletionResponse.choices().getFirst().message().content()), ContentResponse.class);
				}

				// session 구하기
				calculateSession(contentResponse);

				UsageResponse usageResponse = new Gson().fromJson(response, UsageResponse.class);

				// csv 파일 생성
				writeCSV("createPlanChat", request, contentResponse, usageResponse, responseFormatMismatch);

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

	private void writeCSV(String type, PromptEngineeringRequest request, ContentResponse contentResponse,
		UsageResponse usageResponse, boolean responseFormatMismatch) {

		// 디렉토리 경로 생성 및 확인
		String dirPath = csvFilePath + File.separator + type + File.separator + request.getUsername();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			try {
				dir.mkdirs(); // 디렉토리 생성
			} catch (Exception e) {
				throw new CsvFileWriteException("디렉토리 생성에 실패했습니다. -> " + dirPath);
			}
		}

		String filePath = dirPath + File.separator + request.getFilename();

		// try-catch-resource로도 할 수 있으나 직관적으로 보이기 위해 이렇게 함
		CSVWriter writer = null;
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(filePath, true); // true: append mode
			osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			writer = new CSVWriter(osw);

			// 파일이 비어 있는 경우 컬럼명 작성
			if (new File(filePath).length() == 0) {
				writer.writeNext(csvColumns);
			}

			String system = request.getSystemMessage();
			String responseFormat = request.getResponseFormat();
			String reqStr = new Gson().toJson(request.getContentRequest());
			String resStr = new Gson().toJson(contentResponse);
			String usage = new Gson().toJson(usageResponse);

			String[] record = {
				system == null ? "null" : system,
				responseFormat == null ? "null" : responseFormat,
				reqStr == null ? "null" : reqStr,
				resStr == null ? "null" : resStr,
				usage == null ? "null" : usage,
				responseFormatMismatch ? "TRUE" : "FALSE"
			};
			writer.writeNext(record);

		} catch (IOException e) {
			// 예외 처리 추가
			throw new CsvFileWriteException("CSV 파일 작성 중 오류가 발생했습니다.");

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
				throw new CsvFileWriteException("자원 해제 중 오류가 발생했습니다.");
			}
		}
	}
}
