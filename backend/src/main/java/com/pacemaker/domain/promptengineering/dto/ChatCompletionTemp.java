package com.pacemaker.domain.promptengineering.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionTemp(

	@JsonProperty("max_tokens")
	@Schema(description = "생성할 최대 토큰 수", example = "2048")
	Integer maxTokens,

	@Schema(description = "샘플링 온도를 설정하는 것. 값이 높을수록 모델이 더 많은 리스크를 감수함", example = "0.9")
	Double temperature
) {
}
