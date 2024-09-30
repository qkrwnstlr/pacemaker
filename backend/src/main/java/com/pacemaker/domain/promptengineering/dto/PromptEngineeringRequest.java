package com.pacemaker.domain.promptengineering.dto;

import com.pacemaker.domain.openai.dto.ChatCompletionRequest;
import com.pacemaker.domain.plan.dto.ContentRequest;

public class PromptEngineeringRequest {

	private String path;

	private ChatCompletionRequest chatCompletionRequest;

	private ContentRequest contentRequest;

	private String systemMessage;

	private String responseFormat;

}
