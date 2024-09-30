package com.pacemaker.domain.promptengineering.dto;

import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.plan.dto.ContentRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PromptEngineeringRequest {

	@NotNull
	private String name;

	@NotNull
	private String filename;

	@NotNull
	private ChatCompletionRequest chatCompletionRequest;

	@NotNull
	private ContentRequest contentRequest;

	@NotNull
	private String systemMessage;

	@NotNull
	private String responseFormat;

}
