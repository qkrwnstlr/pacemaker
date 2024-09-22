package com.pacemaker.domain.openai.dto;

import java.util.List;

public record OpenAiResponse(List<Choice> choices) {
}
