package com.pacemaker.domain.plan.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ContentResponse {

	@NotNull
	private String message;

	@NotNull
	private Context context;

	@NotNull
	private Plan plan;

	@Getter
	public class Context {
		private String goal;
		private Integer goalTime;
		private Integer goalDistance;
		private List<String> trainDayOfWeek;
		private UserInfo userInfo;
	}

	@Getter
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
	public class Plan {
		private Integer totalDays;
		private Integer totalTimes;
		private Integer totalDistances;
		private List<PlanTrain> planTrains;
	}

	@Getter
	public class PlanTrain {
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
				calculateSessionTime();
				return;
			}

			if ("distance".equals(paramType)) {
				calculateSessionDistance();
				return;
			}

			// 예외 상황
			return;
		}

		private void calculateSessionTime() {
			// 총 시간 계산 로직
			this.sessionTime = trainParam * repetition + interParam * (repetition - 1);
			this.sessionDistance = 0; // 아직 기준이 없어서 0 (기준을 정한다면 조깅할 때 페이스? 이런거?)
		}

		private void calculateSessionDistance() {
			// 총 거리 계산 로직
			this.sessionTime = 0; // 위와 동일
			this.sessionDistance = trainParam * repetition + interParam * (repetition - 1);
		}
	}
}
