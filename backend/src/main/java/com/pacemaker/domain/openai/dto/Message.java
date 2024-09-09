package com.pacemaker.domain.openai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Message(
	@NotNull String role, // "user" 또는 "assistant"
	@NotNull String content // 대화 내용
) {
}
