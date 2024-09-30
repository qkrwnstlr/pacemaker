package com.pacemaker.domain.promptengineering.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsageResponse {

	Usage usage;

	@Getter
	@Setter
	public static class Usage {
		@SerializedName("prompt_tokens") // Jackson은 JsonProperty지만 Gson은 SerializedName임!
		Integer promptTokens;

		@SerializedName("completion_tokens")
		Integer completionTokens;

		@SerializedName("total_tokens")
		Integer totalTokens;

		@SerializedName("completion_tokens_details")
		CompletionTokensDetails completionTokensDetails;

		@Getter
		@Setter
		public static class CompletionTokensDetails {
			@SerializedName("reasoning_tokens")
			Integer reasoningTokens;
		}
	}
}
