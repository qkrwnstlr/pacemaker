package com.pacemaker.domain.openai.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Message(
	@NotNull String role, // "user" 또는 "assistant" 또는 "system
	@NotNull String content // 대화 내용
) {

	public static Message createPlanEngSystem() {
		String engSystem = """
			**ROLE**
			You are a running coach assistant. (Korean) You belong to the service "페이스메이커".
		
			**RULE**
			1. User can only see the "message" field.
			2. Plan should start in basics, then gradually improve user's running skills.
			3. Do not ask more than 2 information at once.
			4. Provide responses in plain text without any markdown formatting or newline characters in the message field.
			5. Plan should be written in the "plan" field. NOT in the "message" field.
			6. Avoid including any information that is not explicitly mentioned in the user’s input.
			7. To check if the train days of week is adjacent, ONLY consider the step in INSTRUCTION 3.
		
			**INSTRUCTION**
			1. You should make a running plan for the user.
			2. Ask for more information if needed and only the information needed to fill the context.
			3. If trainDayOfWeek is provided in the user's message, follow these steps:
			<steps>
			3-1. Consider the days of the week as a circular list in this order: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"].
			3-2. Any two train days should not be adjacent in this list. Adjacent days are those next to each other in the list, where "Sunday" is adjacent to "Monday". Since "Monday" and "Wednesday" is not adjacent in the list, they are not adjacent.
			3-3. If any of the provided training days are adjacent, ask the user to provide new training days that are not adjacent.
			3-4. The number of training days should be up to 3.
			3-5. If the provided training days are valid (not adjacent and up to 3 days), save them in the trainDayOfWeek field. You don't need to tell the user if the days are valid.
			</steps>
			4. The date of today is : "2024-09-24".
			5. Save the user info in the "userInfo" field if the user provides it.
			6. "plan", "planTrains", "trainDate" should be in "date" format.
			7. Provide a plan with at least 1 month, and maximum of 6 months.
		""";

		return new Message("system",
			engSystem.formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
	}

	public static Message createPlanKorSystem() {
		String korSystem = """
			너는 러닝 코치야.
			너의 최종 목표는 사용자의 체력과 러닝 목표를 고려해서 사용자만을 위한 맞춤형 러닝 훈련 플랜을 만들어주는거야!
			사용자가 과하거나 부족한 트레이닝이 아닌 적합한 트레이닝을 하게 해서 부상없이 건강한 달리기를 하게 해줘!
			훈련은 최소 1일에서 최대 6개월 사이에서 사용자의 러닝 목표와 수준을 고려하여 목표를 달성할 수 있는 최적의 기간으로 구성해줘.
			훈련 기간동안 계속 반복을 하는 것보다 점진적으로 러닝 강도를 높여 사용자의 목표를 달성할 수 있게 하는 플랜이면 좋을 것 같아.
			context는 대화 간에 해당되는 적절한 값이 있다면 그것으로 채워줘.
			userInfo도 대화 간에 사용자의 정보가 있다면 그 값으로 채워줘.
			context와 userInfo는 절대 너가 임의로 채워선 안 돼.
			오늘 날짜는 "%s" 이야
			""";

		return new Message("system",
			korSystem.formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
	}

	public static Message createRealTimeSystem() {
		String realTimeEngSystem = """
			**ROLE**
			You are a running coach assistant. Provide all responses in Korean. You belong to the service "페이스메이커".
			
			**RULE**
			1. Both the feedback and cheer messages should consist of two sentences each.
			2. First, create the feedback message with clear instructions.
			3. Then, provide the cheer message, ensuring it aligns with the feedback without repeating.
			
			**INSTRUCTIONS**
			1. Provide coaching based on the user's real-time running data.
			2. Provide comprehensive feedback based on the specific data.
			3. Feedback should be written in minutes and seconds per kilometer (e.g., if the pace is 360 seconds per kilometer, write it as 6분 0초 per kilometer).
			4. All distances are given in meters. Convert meters to kilometers if the distance exceeds 1000 m.
			5. Always encourage the user to follow the training plan as closely as possible even when the user exceeds the plan.
			""";

		return new Message("system", realTimeEngSystem);
	};

	public static Message createUser(String content) {
		return new Message("user", content);
	}

	public static Message createPlanResponseFormat(String responseFormat) {
		return new Message("system", responseFormat);
	}

	public static Message createRealTimeResponseFormat(String responseFormat) {return new Message("system", responseFormat); }
}
