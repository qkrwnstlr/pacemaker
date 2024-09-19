package com.pacemaker.domain.openai.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ChatRequest {

	@NotNull
	private String message;

	@NotNull
	private Context context;

	@NotNull
	private Plan plan;

	@Data
	@Builder
	public static class Context {
		private String goal;
		private Integer goalTime;
		private Integer goalDistance;

		@NotNull
		private List<String> trainDayOfWeek;

		private UserInfo userInfo;

		@Data
		@Builder
		public static class UserInfo {
			private Integer age;
			private Integer height;
			private Integer weight;
			private Integer gender;
			private String injuries;
		}
	}

	@Data
	@Builder
	public static class Plan {
		private Integer totalDays;
		private Integer totalTimes;
		private List<TrainDetail> trainDetails;

		@Data
		@Builder
		public static class TrainDetail {
			private Integer index;
			private String trainDate;
			private Integer pace;
			private Integer trainDistance;
			private Integer trainDuration;
			private Integer trainParam;
			private Integer repeat;
			private Integer interParam;
		}
	}
}
