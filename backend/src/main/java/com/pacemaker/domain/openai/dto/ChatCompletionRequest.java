package com.pacemaker.domain.openai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 제거
@Builder
public record ChatCompletionRequest(
	@NotNull String model, // 사용할 모델의 이름 ("gpt-3.5-turbo", "gpt-4o-mini")
	@NotNull List<Object> messages, // 대화 메시지 배열
	@JsonProperty("max_tokens") Integer maxTokens, // 생성할 최대 토큰 수
	Double temperature, // 샘플링 온도를 설정하는 것. 값이 높을수록 모델이 더 많은 리스크를 감수함
	@JsonProperty("top_p") Double topP, // 온도 샘플링에 대한 대안으로, 누적 확률 질량의 상위 p%에 해당하는 토큰들만 고려하는 nucleus 샘플링 방식
	Integer n, // 생성할 응답 수
	Boolean stream, // 부분적인 진행 상태를 스트리밍할지 여부
	Integer logprobs, // 가장 가능성 높은 토큰에 대한 로그 확률을 포함함
	Boolean echo, // 프롬프트와 함께 완성(답변)을 반영함
	List<String> stop, // API가 추가 토큰 생성을 중지할 최대 4개의 시퀀스를 설정할 수 있음
	@JsonProperty("presence_penalty") Double presencePenalty, // 0과 1 사이의 값으로, 해당 텍스트에 이미 나타난 새로운 토큰을 페널티 부과
	@JsonProperty("frequency_penalty") Double frequencyPenalty, // 0과 1 사이의 값으로, 해당 텍스트에서 이미 등장한 빈도에 따라 새로운 토큰에 페널티를 부과
	@JsonProperty("best_of") Integer bestOf, // 서버 측에서 가장 좋은 결과를 선택하고 반환하는 방식으로, "best" (토큰당 가장 낮은 로그 확률을 가진 것)를 선택함
	@JsonProperty("logit_bias") Map<String, Integer> logitBias, // 지정된 토큰이 생성될 확률을 수정함
	String user, // OpenAI가 남용을 감시하고 탐지할 수 있도록 도와주는 고유한 사용자 식별자
	@JsonProperty("response_format") String responseFormat // response_format
) {
}
