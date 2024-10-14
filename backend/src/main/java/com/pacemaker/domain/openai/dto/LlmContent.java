package com.pacemaker.domain.openai.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class LlmContent {

	@NotNull
	private String message;

	@NotNull
	private Context context;

	@NotNull
	private Plan plan;

	@Getter
	@Setter
	public class Context {
		private String goal;
		private Integer goalTime;
		private Integer goalDistance;
		private List<String> trainDayOfWeek;
		private UserInfo userInfo;
	}

	@Getter
	@Setter
	public class UserInfo {
		private Integer age;
		private Integer height;
		private Integer weight;
		private String gender;
		private List<String> injuries;
		private Integer recentRunPace;
		private Integer recentRunDistance;
		private Integer recentRunHeartRate;
	}

	@Getter
	@Setter
	public class Plan {
		private List<Integer> index;
		private List<String> trainDate;
		private List<String> paramType;
		private List<Integer> repetition;
		private List<Integer> trainParam;
		private List<Integer> trainPace;
		private List<Integer> interParam;
	}
}