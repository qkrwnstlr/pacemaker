package com.pacemaker.domain.openai.dto;

import java.util.List;

public record ChatCompletionResponse(List<Choice> choices) {
	public record Choice(int index, Message message, Object logprobs, String finishReason) {
	}
}

