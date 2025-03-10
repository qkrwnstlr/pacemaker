package com.pacemaker.domain.plan.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pacemaker.global.util.TempValue;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ContentResponse {

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
	public static class Plan {
		private Integer totalDays;
		private Integer totalTimes;
		private Integer totalDistances;
		private List<PlanTrain> planTrains;
	}

	@Getter
	@Setter
	public static class PlanTrain {
		private Integer index;
		private String trainDate;
		private String paramType;
		private Integer sessionTime;
		private Integer sessionDistance;
		private Integer repetition;
		private Integer trainParam;
		private Integer trainPace;
		private Integer interParam;

		public void calculateSession() {
			if (repetition == null || repetition < 1) {
				repetition = 1;
			}

			if ("time".equals(paramType)) {
				paramTypeTime();
				return;
			}

			if ("distance".equals(paramType)) {
				paramTypeDistance();
				return;
			}

			// 예외 상황
			return;
		}

		private void paramTypeTime() {
			// 총 시간 계산 로직
			this.sessionTime = trainParam * repetition + interParam * (repetition - 1);
			this.sessionDistance = (int)Math.round(trainParam * 1000.0 / trainPace * repetition)
				+ (int)Math.round(interParam * 1000.0 / TempValue.JOGGING_PACE * (repetition - 1));
		}

		private void paramTypeDistance() {
			// 총 거리 계산 로직
			this.sessionTime = (int)Math.round(trainParam / 1000.0 * trainPace * repetition)
				+ (int)Math.round(interParam / 1000.0 * (repetition - 1));
			this.sessionDistance = trainParam * repetition + interParam * (repetition - 1);
		}
	}
}
