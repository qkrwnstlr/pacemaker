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
			private List<String> injuries;
			private Integer recentRunPace;
			private Integer recentRunDistance;
			private Integer recentRunHeartRate;
		}
	}

	@Data
	@Builder
	public static class Plan {
		private Integer totalDays;
		private Integer totalTimes;
		private List<TrainDetail> trainDetails;
// "plan": {
// 			"totalDays": null,
// 				"totalTimes": null,
// 				"trainDetails": {
// 					"iT": null,
// 					"tP": null,
// 					"tT": null,
// 			}
// 		}
		@Data
		@Builder
		public static class TrainDetail {
			private Integer idx; // 인덱스
			private String tD; // trainDate
			private Integer pace; // pace
			private Integer tDist; // trainDistance
			private Integer tDur; // trainDuration
			private Integer tP; // trainParam
			private Integer r; // repeat
			private Integer interParam; // interParam
		}
	}
}
