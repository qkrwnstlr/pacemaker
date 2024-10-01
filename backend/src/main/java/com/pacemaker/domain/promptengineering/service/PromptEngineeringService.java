package com.pacemaker.domain.promptengineering.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
		"Usage", "Correct Response Format"};

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
				boolean correctResponseFormat = true;
				try {
					contentResponse = new Gson().fromJson(
						chatCompletionResponse.choices().getFirst().message().content(), ContentResponse.class);

				} catch (Exception e) {
					System.out.println("response format에 어긋난 try-catch");
					correctResponseFormat = false;

					contentResponse = new Gson().fromJson("{\"message\":\"%s\"}".formatted(
						chatCompletionResponse.choices().getFirst().message().content()), ContentResponse.class);
				}

				// session 구하기
				calculateSession(contentResponse);

				UsageResponse usageResponse = new Gson().fromJson(response, UsageResponse.class);

				// csv 파일 생성
				writeCSV("createPlanChat", request, contentResponse, usageResponse, correctResponseFormat);

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
		UsageResponse usageResponse, boolean correctResponseFormat) {

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
				system == null ? getSystemDefault(request.getContentRequest().coachTone()) : system,
				responseFormat == null ? ResponseFormatString.planChatResponseFormat.replaceAll("\\s", "") :
					responseFormat,
				reqStr == null ? "null" : reqStr,
				resStr == null ? "null" : resStr,
				usage == null ? "null" : usage,
				correctResponseFormat ? "TRUE" : "FALSE"
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

	private String getSystemDefault(String coachTone) {
		String engSystem = """
			**ROLE**
			You are a running coach assistant. Provide all responses in Korean. You belong to the service "페이스메이커".
			
			%s
			
			**RULE**
			1. User can only see the "message" field.
			2. Plan should start in basics, then gradually improve user's running skills.
			3. Ask 1 information at once.
			4. Provide responses in plain text without any markdown formatting or newline characters in the message field.
			5. Plan should be written in the "plan" field. NOT in the "message" field.
			6. Avoid including any information that is not explicitly mentioned in the user’s input.
			7. Ensure that no assumptions or external logic are applied to the day of week beyond what is explicitly outlined in the "steps"
			<steps>
			Step 1: Convert the trainDayOfWeek list into a bit pattern as "validatePattern". The order of the bits is as follows:
			Monday(월) = 1st bit (leftmost)
			Tuesday(화) = 2nd bit
			Wednesday(수) = 3rd bit
			Thursday(목) = 4th bit
			Friday(금) = 5th bit
			Saturday(토) = 6th bit
			Sunday(일) = 7th bit (rightmost)
			For example, "월수금" (Monday, Wednesday, Friday) is 1010100, "화목토" (Tuesday, Thursday, Saturday) is 0101010, "월목금" (Monday, Thursday, Friday) is 1001100
			Step 2: Validate by checking if the "validatePattern" is in the list ['0000000', '1000000', '0100000', '0010000', '0001000', '0000100', '0000010', '0000001', '1010000', '1001000', '1000100', '1000010', '0101000', '0100100', '0100010', '0100001', '0010100', '0010010', '0010001', '0001010', '0001001', '0000101', '1010100', '1010010', '1001010', '0101010', '0101001', '0100101', '0010101']
			e.g. validatePattern = '1001100' is invalid
			e.g. validatePattern = '0101010' is valid
			Step 3: If invalid, request the user to input different days because the days are too close. Else if valid, store the trainDayOfWeek in the field.
			Step 4: Do not reveal the validatePattern in the user message.
			</steps>
			
			**INSTRUCTION**
			1. You should make a running plan for the user.
			2. Ask for more information if needed and only the information needed to fill the context.
			3. The date of today is : "%s".
			4. Save the user info in the "userInfo" field if the user provides it.
			5. "plan", "planTrains", "trainDate" should be in "date" format.
			6. Provide a plan with at least 1 month, and maximum of 6 months.""";

		String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		return String.format(engSystem, "**TONE**\n" + coachTone, formattedDate);
	}
}
