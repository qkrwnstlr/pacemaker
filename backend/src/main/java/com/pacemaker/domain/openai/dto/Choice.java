package com.pacemaker.domain.openai.dto;

public record Choice(int index, Message message, Object logprobs, String finishReason) {
}
