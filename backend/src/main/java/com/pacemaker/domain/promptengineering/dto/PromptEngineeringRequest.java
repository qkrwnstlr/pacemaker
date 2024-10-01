package com.pacemaker.domain.promptengineering.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class PromptEngineeringRequest {

	@NotNull
	@Schema(description = "당신의 이름", example = "kyw")
	private String username;

	@NotNull
	@Schema(description = "csv 파일명", example = "v1.csv")
	private String filename;

	@NotNull
	@Schema(description = "openai 파라미터")
	private ChatCompletionTemp chatCompletionRequest;

	@NotNull
	@Schema(description = "채팅 DTO")
	private ContentRequestTemp contentRequest;

	@Setter
	@Schema(description = "system 프롬프팅", example = "key까지 아예 없애면 default로 들어감 (nullable)")
	private String systemMessage;

	@Setter
	@Schema(description = "response format", example = "key까지 아예 없애면 default로 들어감 (nullable)")
	private String responseFormat;

}
