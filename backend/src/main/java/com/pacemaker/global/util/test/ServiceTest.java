package com.pacemaker.global.util.test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pacemaker.domain.plan.dto.ContentResponse;
import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.entity.PlanTrainStatus;
import com.pacemaker.global.exception.InvalidDayOfWeekException;

import lombok.Data;

public class ServiceTest {

	@Data
	static class PlanCustom {
		List<PlanTrainCustom> planTrains = new ArrayList<>();
		String context;
		LocalDate createdAt;
		LocalDate expiredAt;

		public void updatePlanCreatedAt(LocalDate createdAt) {
			this.createdAt = createdAt;
		}

		public void updatePlanExpiredAt(LocalDate expiredAt) {
			this.expiredAt = expiredAt;
		}
	}

	@Data
	static class PlanTrainCustom {
		LocalDate trainDate;
		PlanTrainStatus status;

		public void updateTrainDate(LocalDate trainDate) {
			this.trainDate = trainDate;
		}
	}

	@Data
	static class ContextCustom {
		String goal;
		Integer goalTime;
		Integer goalDistance;
		List<String> trainDayOfWeek;
	}

	static void planPostponeAndPlanTrain(PlanCustom plan) {

		List<PlanTrainCustom> planTrains = plan.getPlanTrains();
		int size = planTrains.size();
		PlanTrainCustom planTrain;

		for (int i = 0; i < size - 1; i++) {
			planTrain = planTrains.get(i);

			// planTrain이 DONE인 경우는 넘기기
			if (planTrain.getStatus() == PlanTrainStatus.DONE) {
				continue;
			}

			// BEFORE가 시작인 지점부터는 이후 모든 PlanTrain은 BEFORE 상태여야함! (서비스 흐름상)
			// 그래서 trainDate가 현재 날짜보다 이전인 경우를 굳이 확인할 필요가 없음
			// 훈련을 하지 못한 PlanTrain 즉 과거 날짜의 trainDate인 PlanTrain은 한 개만 존재한다!! (이것이 보장되지 않으면 문제가 심함)
			planTrain.updateTrainDate(planTrains.get(i + 1).getTrainDate());

			if (i == 0) {
				// 첫 훈련부터 미뤄지는 경우 플랜 시작 날짜 조정
				plan.updatePlanCreatedAt(planTrain.getTrainDate());
			}
		}

		// 쿼리 조건식(where)에 의해서 무조건 미뤄야 하는 plan을 가져오기 때문에 마지막은 당연히 미뤄져야함!
		planTrain = planTrains.get(size - 1);

		// 다음 날짜는 사용자의 요일 선택에 의해 동적으로 할당되어야 함
		DayOfWeek currentDayOfWeek = planTrain.getTrainDate().getDayOfWeek();
		ContextCustom context = new Gson().fromJson(plan.getContext(), ContextCustom.class);
		List<String> trainDayOfWeek = context.getTrainDayOfWeek();

		// 다음 요일 가져오기
		DayOfWeek nextDayOfWeek = getNextDayOfWeek(currentDayOfWeek, trainDayOfWeek);

		// 다음 요일에 해당하는 날짜로 업데이트
		LocalDate nextTrainDate = planTrain.getTrainDate().with(TemporalAdjusters.next(nextDayOfWeek));
		planTrain.updateTrainDate(nextTrainDate);

		// 마지막 훈련의 날짜를 기준으로 플랜 종료 날짜 조정
		plan.updatePlanExpiredAt(planTrain.getTrainDate());
	}

	static DayOfWeek getNextDayOfWeek(DayOfWeek currentDayOfWeek, List<String> trainDayOfWeek) {
		// 요일 이름을 DayOfWeek enum으로 변환
		List<DayOfWeek> selectedDays = trainDayOfWeek.stream()
			.map(day -> {
				try {
					return DayOfWeek.valueOf(day.toUpperCase());
				} catch (IllegalArgumentException e) {
					throw new InvalidDayOfWeekException("유효하지 않은 요일 값: " + day);
				}
			})
			.sorted()
			.toList();

		// 현재 요일 이후로 선택한 요일 중 가장 빠른 요일 찾기
		for (DayOfWeek day : selectedDays) {
			if (day.getValue() > currentDayOfWeek.getValue()) {
				return day; // 현재 요일 이후로 오는 가장 빠른 요일 반환
			}
		}

		// 만약 현재 요일보다 뒤에 있는 요일이 없으면, 첫 번째 요일로 돌아감
		// [화, 목] 이고 현재 목요일이라면 화요일이 반환되고
		// [화, 목, 일] 이고 현재 일요일 이라면 화요일이 반환됨
		return selectedDays.get(0); // 자바 17버전을 기준으로 하면 getFirst()가 없다고 함! 21버전 이후로 생겼다고 하니 알아두면 좋을듯!!
	}

	public static void main(String[] args) {
		PlanCustom plan = new PlanCustom();
		plan.setContext("""
			{"goal":"10km 마라톤 완주","goalTime":0,"goalDistance":10000,"trainDayOfWeek":["Wednesday","Saturday","Monday"]}""");
		plan.setCreatedAt(LocalDate.of(2024, 9, 30));
		plan.setExpiredAt(LocalDate.of(2024, 10, 30));

		LocalDate date[] = {
			LocalDate.of(2024, 9, 30), // 0
			LocalDate.of(2024, 10, 2), // 1
			LocalDate.of(2024, 10, 5), // 2
			LocalDate.of(2024, 10, 7), // 3
			LocalDate.of(2024, 10, 9), // 4
			LocalDate.of(2024, 10, 12), // 5
			LocalDate.of(2024, 10, 14), // 6
			LocalDate.of(2024, 10, 16), // 7
			LocalDate.of(2024, 10, 19), // 8
			LocalDate.of(2024, 10, 21), // 9
			LocalDate.of(2024, 10, 23), // 10
			LocalDate.of(2024, 10, 26), // 11
			LocalDate.of(2024, 10, 28), // 12
			LocalDate.of(2024, 10, 30), // 13
		};

		PlanTrainStatus status[] = {
			PlanTrainStatus.DONE, // 0
			PlanTrainStatus.DONE, // 1
			PlanTrainStatus.BEFORE, // 2
			PlanTrainStatus.BEFORE, // 3
			PlanTrainStatus.BEFORE, // 4
			PlanTrainStatus.BEFORE, // 5
			PlanTrainStatus.BEFORE, // 6
			PlanTrainStatus.BEFORE, // 7
			PlanTrainStatus.BEFORE, // 8
			PlanTrainStatus.BEFORE, // 9
			PlanTrainStatus.BEFORE, // 10
			PlanTrainStatus.BEFORE, // 11
			PlanTrainStatus.BEFORE, // 12
			PlanTrainStatus.BEFORE, // 13
		};

		for (int i = 0; i < date.length; i++) {
			PlanTrainCustom planTrain = new PlanTrainCustom();
			planTrain.setTrainDate(date[i]);
			planTrain.setStatus(status[i]);

			plan.getPlanTrains().add(planTrain);
		}

		// 플랜 미루기
		planPostponeAndPlanTrain(plan);

		// 출력

		System.out.println("plan.CreatedAt = " + plan.getCreatedAt());
		System.out.println("plan.ExpiredAt = " + plan.getExpiredAt());
		for (PlanTrainCustom planTrain : plan.getPlanTrains()) {
			System.out.println(planTrain.toString());
		}
	}
}
