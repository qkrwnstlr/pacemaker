package com.pacemaker.domain.plan.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.pacemaker.domain.plan.dto.ActivePlanTrainResponse;
import com.pacemaker.domain.plan.dto.ContentRequest;
import com.pacemaker.domain.plan.dto.ContentResponse;
import com.pacemaker.domain.plan.dto.CreatePlanRequest;
import com.pacemaker.domain.plan.dto.CreatePlanResponse;
import com.pacemaker.domain.plan.dto.ProgressPlanResponse;
import com.pacemaker.domain.plan.dto.UpdatePlanRequest;
import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.entity.PlanStatus;
import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.entity.PlanTrainStatus;
import com.pacemaker.domain.plan.repository.PlanRepository;
import com.pacemaker.domain.plan.repository.PlanTrainRepository;
import com.pacemaker.domain.report.dto.PlanTrainResponse;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.user.repository.UserRepository;
import com.pacemaker.global.exception.ActivePlanNotFoundException;
import com.pacemaker.global.exception.InvalidDateException;
import com.pacemaker.global.exception.InvalidDayOfWeekException;
import com.pacemaker.global.exception.NotFoundException;
import com.pacemaker.global.exception.PlanAlreadyExistsException;
import com.pacemaker.global.exception.PlanTrainEmptyException;
import com.pacemaker.global.exception.UserMismatchException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PlanService {

	private final PlanRepository planRepository;
	private final UserRepository userRepository;
	private final PlanTrainRepository planTrainRepository;

	@Transactional
	public Long createPlan(CreatePlanRequest createPlanRequest) {

		// uid 존재 체크
		User user = findUserByUid(createPlanRequest.uid());

		// 플랜 존재 체크
		existsActivePlan(user.getId());
		existsNowPlanTrain(user.getId());

		// 유저 정보 업데이트
		ContentRequest.Context.UserInfo userInfo = createPlanRequest.context().userInfo();
		user.update(user.getUsername(), userInfo.age(), userInfo.height(), userInfo.weight(), userInfo.gender(),
			userInfo.injuries().toString());

		// Plan Entity 생성
		Plan planEntity = createPlanEntity(user, createPlanRequest.plan(), createPlanRequest.context());

		// PlanTrain 연결
		for (ContentRequest.Plan.PlanTrain planTrain : createPlanRequest.plan().planTrains()) {
			PlanTrain planTrainEntity = createPlanTrainEntity(planTrain);
			planEntity.addPlanTrain(planTrainEntity);
		}

		Long savedPlanId = planRepository.save(planEntity).getId();
		System.out.println("생성 Plan Id: " + savedPlanId.equals(planEntity.getId()));

		return planEntity.getId();
	}

	@Transactional(readOnly = true)
	public CreatePlanResponse findActivePlanByUid(String uid) {

		Plan findActivePlan = findActivePlan(uid);

		CreatePlanResponse planResponse = CreatePlanResponse.builder()
			.plan(findActivePlan)
			.build();

		int size = findActivePlan.getPlanTrains().size();
		for (int i = 0; i < size; i++) {
			planResponse.addPlanTrain(findActivePlan.getPlanTrains().get(i), i);
		}

		return planResponse;
	}

	@Transactional
	public void deleteActivePlanByUidForTest(String uid) {
		Plan findActivePlan = findActivePlan(uid);

		// PlanTrain 제거
		findActivePlan.getPlanTrains().clear();

		// Plan 제거
		planRepository.delete(findActivePlan);
	}

	@Transactional
	public void deleteActivePlanByUid(String uid) {

		// 사용자의 활성 Plan 찾기
		Plan findActivePlan = findActivePlan(uid);

		List<PlanTrain> planTrains = findActivePlan.getPlanTrains();
		int size = planTrains.size();

		// Before PlanTrain 제거
		for (int i = size - 1; i >= 0; i--) {
			if (planTrains.get(i).getStatus() == PlanTrainStatus.DONE) {
				if (i == size - 1) {
					// 플랜 마감 날짜 수정 (2개만 완료하고 삭제했다면 2개까지의 범위만 progress 범위로 하기 위해!)
					findActivePlan.updatePlanExpiredAt(planTrains.get(i).getTrainDate());
				}

				continue;
			}

			findActivePlan.getPlanTrains().remove(i);
		}

		if (findActivePlan.getPlanTrains().isEmpty()) {
			// 관련된 PlanTrain이 없기 때문에 Plan도 삭제
			planRepository.delete(findActivePlan);

		} else {
			// totalTimes 와 totalDistances 변경
			findActivePlan.updatePlanTrainReport(planRepository.findSumPlanDistanceByPlanId(findActivePlan.getId()),
				planRepository.findSumPlanTimeByPlanId(findActivePlan.getId()));

			// 플랜 삭제 상태로 변경
			findActivePlan.updatePlanStatus(PlanStatus.DELETED);
		}
	}

	@Transactional(readOnly = true)
	public ActivePlanTrainResponse findActivePlanTrainByPlanTrainId(Long id, String uid) {
		User user = findUserByUid(uid);
		PlanTrain planTrain = findPlanTrainById(id);
		Plan plan = findPlanById(planTrain.getPlan().getId());

		if (user != plan.getUser()) {
			throw new UserMismatchException("본인의 레포트만 조회할 수 있습니다.");
		}

		Integer planTrainIndex = findIndexByPlanTrainId(id);

		return ActivePlanTrainResponse.builder()
			.planTrain(PlanTrainResponse.of(planTrainIndex, planTrain))
			.build();
	}

	@Transactional(readOnly = true)
	public ProgressPlanResponse findPlanProgressByUid(String uid, Integer year, Integer month, Integer day) {

		// 입력 받은 값 검증 및 반환
		LocalDate date = createLocalDate(year, month, day);

		// 해당 하는 플랜 찾기
		Plan findPlan = findPlanByUidAndDate(uid, date);

		// 없으면 null
		if (findPlan == null) {
			return null;
		}

		return ProgressPlanResponse.builder()
			.plan(findPlan)
			.build();
	}

	@Transactional
	public void updatePlan(UpdatePlanRequest updatePlanRequest) {

		Plan plan = findActivePlan(updatePlanRequest.uid());
		plan.removeBeforePlanTrains();

		for (ContentRequest.Plan.PlanTrain newPlanTrain : updatePlanRequest.plan().planTrains()) {
			LocalDate trainDate = LocalDate.parse(newPlanTrain.trainDate());

			// trainDate가 오늘 이전이라면 추가하지 않음
			if (trainDate.isBefore(LocalDate.now())) {
				continue;
			}

			// trainDate가 오늘이라면 plan에 오늘 날짜의 planTrain이 이미 존재하면 추가하지 않음
			if (trainDate.isEqual(LocalDate.now())) {
				boolean alreadyExists = false;
				for (PlanTrain existPlanTrain : plan.getPlanTrains()) {
					if (existPlanTrain.getTrainDate().isEqual(LocalDate.now())) {
						alreadyExists = true;
						break;
					}
				}
				if (alreadyExists) {
					continue;
				}
			}

			// trainDate가 오늘이면서 plan에 오늘 날짜의 planTrain이 없는 경우 & 오늘 이후라면 추가
			PlanTrain planTrainEntity = createPlanTrainEntity(newPlanTrain);
			plan.addPlanTrain(planTrainEntity);
		}

		int totalDays = 0;
		int totalTimes = 0;
		int totalDistances = 0;
		LocalDate trainDate = plan.getPlanTrains().get(0).getTrainDate(); // getFirst()는 Java 21 이상 부터 가능
		LocalDate createdAt = trainDate;
		LocalDate expiredAt = trainDate;

		for (PlanTrain planTrain : plan.getPlanTrains()) {
			totalDays++;
			totalTimes += planTrain.getSessionTime() != null ? planTrain.getSessionTime() : 0;
			totalDistances += planTrain.getSessionDistance() != null ? planTrain.getSessionDistance() : 0;

			trainDate = planTrain.getTrainDate();
			createdAt = trainDate.isBefore(createdAt) ? trainDate : createdAt;
			expiredAt = trainDate.isAfter(expiredAt) ? trainDate : expiredAt;
		}

		plan.updatePlanDetails(totalDays, totalTimes, totalDistances, createdAt, expiredAt);

		planRepository.save(plan);
	}

	// @Transactional // -> (배치 작업의 트랜잭션 관리 기능으로 생략 가능)
	public void updatePlan(Plan plan) { // 오버로딩 됨
		// 만약에 스프링 배치 중에 plan이 삭제 된다면???!!!
		// 동시성 제어가 필요해 보임 (lock)
		planRepository.save(plan);
	}

	// @Transactional(readOnly = true) // -> (배치 작업의 트랜잭션 관리 기능으로 생략 가능)
	public List<Plan> getPlansForPostpone() {
		return planRepository.findPlansForPostpone();
	}

	// @Transactional // -> (배치 작업의 트랜잭션 관리 기능으로 생략 가능)
	public void planPostponeAndPlanTrain(Plan plan) {

		List<PlanTrain> planTrains = plan.getPlanTrains();
		int size = planTrains.size();
		PlanTrain planTrain;
		// 여기 우선 안정될 때까지 sout 남겨두기!!
		System.out.println("plan.id: " + plan.getId());
		System.out.println("size: " + size);
		for (int i = 0; i < size - 1; i++) {
			planTrain = planTrains.get(i);
			// 여기도 안정될 때까지 sout 남겨두기!!
			System.out.println(planTrain.getTrainDate());
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
		ContentResponse.Context context = new Gson().fromJson(plan.getContext(), ContentResponse.Context.class);
		List<String> trainDayOfWeek = context.getTrainDayOfWeek();

		// 다음 요일 가져오기
		DayOfWeek nextDayOfWeek = getNextDayOfWeek(currentDayOfWeek, trainDayOfWeek);

		// 다음 요일에 해당하는 날짜로 업데이트
		LocalDate nextTrainDate = planTrain.getTrainDate().with(TemporalAdjusters.next(nextDayOfWeek));
		planTrain.updateTrainDate(nextTrainDate);

		// 마지막 훈련의 날짜를 기준으로 플랜 종료 날짜 조정
		plan.updatePlanExpiredAt(planTrain.getTrainDate());
	}

	private DayOfWeek getNextDayOfWeek(DayOfWeek currentDayOfWeek, List<String> trainDayOfWeek) {
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

	private User findUserByUid(String uid) {
		return userRepository.findByUid(uid)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
	}

	private void existsActivePlan(Long userId) {
		if (planRepository.existsActivePlan(userId)) {
			throw new PlanAlreadyExistsException("해당 사용자는 이미 플랜이 존재합니다.");
		}
	}

	private void existsNowPlanTrain(Long userId) {
		if (planTrainRepository.existsNowPlanTrain(userId)) {
			throw new PlanAlreadyExistsException("해당 사용자는 이미 플랜이 존재합니다. (오늘 plan train 존재)");
		}
	}

	private Plan createPlanEntity(User userInfo, ContentRequest.Plan plan, ContentRequest.Context context) {
		List<ContentRequest.Plan.PlanTrain> planTrains = plan.planTrains();

		if (planTrains == null || planTrains.isEmpty()) {
			throw new PlanTrainEmptyException("플랜에는 최소한 하나의 훈련 세션이 필요합니다.");
		}

		int totalDays = 0;
		int totalTimes = 0;
		int totalDistance = 0;
		LocalDate trainDate = LocalDate.parse(planTrains.get(0).trainDate()); // getFirst()는 Java 21 이상 부터 가능
		LocalDate createdAt = trainDate;
		LocalDate expiredAt = trainDate;
		
		for (ContentRequest.Plan.PlanTrain planTrain : planTrains) {
			totalDays++;
			totalTimes += planTrain.sessionTime() != null ? planTrain.sessionTime() : 0;
			totalDistance += planTrain.sessionDistance() != null ? planTrain.sessionDistance() : 0;

			trainDate = LocalDate.parse(planTrain.trainDate());
			createdAt = trainDate.isBefore(createdAt) ? trainDate : createdAt;
			expiredAt = trainDate.isAfter(expiredAt) ? trainDate : expiredAt;
		}

		return Plan.builder()
			.user(userInfo)
			.createdAt(createdAt)
			.expiredAt(expiredAt)
			.totalDays(totalDays)
			.totalTimes(totalTimes)
			.totalDistances(totalDistance)
			.context(new Gson().toJson(ContentRequest.Context.builder()
				.goal(context.goal())
				.goalDistance(context.goalDistance())
				.goalTime(context.goalTime())
				.trainDayOfWeek(context.trainDayOfWeek())
				.build()))
			.build();
	}

	private PlanTrain createPlanTrainEntity(ContentRequest.Plan.PlanTrain planTrain) {
		return PlanTrain.builder()
			.trainDate(LocalDate.parse(planTrain.trainDate()))
			.paramType(planTrain.paramType())
			.sessionTime(planTrain.sessionTime())
			.sessionDistance(planTrain.sessionDistance())
			.repetition(planTrain.repetition())
			.trainParam(planTrain.trainParam())
			.trainPace(planTrain.trainPace())
			.interParam(planTrain.interParam())
			.build();
	}

	private Plan findActivePlan(String uid) {
		return planRepository.findActivePlan(uid)
			.orElseThrow(() -> new ActivePlanNotFoundException("활성 플랜을 찾을 수 없습니다."));
	}

	private Plan findPlanById(Long id) {
		return planRepository.findPlanById(id)
			.orElseThrow(() -> new NotFoundException("해당 플랜을 찾을 수 없습니다."));
	}

	private PlanTrain findPlanTrainById(Long id) {
		return planTrainRepository.findPlanTrainById(id)
			.orElseThrow(() -> new NotFoundException("해당 훈련을 찾을 수 없습니다."));
	}

	private Integer findIndexByPlanTrainId(Long planTrainId) {
		Integer index = planTrainRepository.findIndexByPlanTrainId(planTrainId);

		if (index == -1) {
			throw new NotFoundException("해당 훈련을 찾을 수 없습니다.");
		}

		return index;
	}

	private LocalDate createLocalDate(Integer year, Integer month, Integer day) {
		try {
			return LocalDate.of(year, month, day);

		} catch (Exception e) {
			throw new InvalidDateException("올바르지 않은 날짜입니다. : " + year + "-" + month + "-" + day);
		}
	}

	private Plan findPlanByUidAndDate(String uid, LocalDate date) {
		return planRepository.findPlanByUidAndDate(uid, date)
			.orElse(null);
	}
}
