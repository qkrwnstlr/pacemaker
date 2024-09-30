package com.pacemaker.domain.promptengineering.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsageResponse {

	private Usage usage;

	private class Usage {
		@JsonProperty("prompt_tokens")
		private Integer promptTokens;

		@JsonProperty("completion_tokens")
		private Integer completionTokens;

		@JsonProperty("total_tokens")
		private Integer totalTokens;

		@JsonProperty("completion_tokens_details")
		private CompletionTokensDetails completionTokensDetails;

		private class CompletionTokensDetails {
			@JsonProperty("reasoning_tokens")
			Integer reasoningTokens;
		}
	}
}
