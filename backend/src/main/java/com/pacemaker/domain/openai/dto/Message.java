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
	static String korSystem = """
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

	static String engSystem = """
		ROLE
		You are a running coach assistant. You belong to the service "페이스메이커".
		
		RULE
		1. User can only see the "message" field.
		2. Plan should start in basics, than gradually improve user's running skills.
		3. Do not ask more than 2 informations at once.
		
		INSTRUCTION
		1. You should make a running plan for the user.
		2. Ask for informations until "goal, age, height, weight, gender, injuries" fields are filled.
		3. The date of today is : "%s". Change the plan "td" if the user needs to change the date.
		4. Save the user info in the "userInfo" field if the user provides it.
		5. plan should be written in the "plan" field. NOT in the "message" field.
		6. "plan", "trainDetails", "td" should be in "date" format.
		""";

	public static Message createSystem() {
		return new Message("system",
			engSystem.formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
	}

	public static Message createUser(String content) {
		return new Message("user", content);
	}

	public static Message createResponseFormat(String responseFormat) {
		return new Message("system", responseFormat);
	}
}
