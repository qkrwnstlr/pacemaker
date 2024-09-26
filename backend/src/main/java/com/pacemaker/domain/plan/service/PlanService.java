package com.pacemaker.domain.plan.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.pacemaker.domain.plan.dto.ContentRequest;
import com.pacemaker.domain.plan.dto.CreatePlanRequest;
import com.pacemaker.domain.plan.dto.PlanResponse;
import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.repository.PlanRepository;
import com.pacemaker.domain.plan.repository.PlanTrainRepository;
import com.pacemaker.domain.report.dto.PlanTrainResponse;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.user.repository.UserRepository;
import com.pacemaker.global.exception.ActivePlanNotFoundException;
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
		existsByUserId(user.getId());

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
	public PlanResponse findActivePlanByUid(String uid) {
		Plan findActivePlan = findActivePlan(uid);

		PlanResponse planResponse = PlanResponse.builder()
			.plan(findActivePlan)
			.build();

		int size = findActivePlan.getPlanTrains().size();
		for (int i = 0; i < size; i++) {
			planResponse.getPlanTrains().add(PlanResponse.PlanTrainDTO.builder()
				.planTrain(findActivePlan.getPlanTrains().get(i))
				.index(i)
				.build());
		}

		return planResponse;
	}

	@Transactional
	public void deleteActivePlanByUid(String uid) {
		Plan findActivePlan = findActivePlan(uid);

		// PlanTrain 제거
		findActivePlan.getPlanTrains().clear();

		// Plan 제거
		planRepository.delete(findActivePlan);
	}

	@Transactional(readOnly = true)
	public PlanTrainResponse findActivePlanTrainByPlanTrainId(Long id, String uid) {
		User user = findUserByUid(uid);
		PlanTrain planTrain = findPlanTrainById(id);
		Plan plan = findPlanById(planTrain.getPlan().getId());

		if (user != plan.getUser()) {
			throw new UserMismatchException("본인의 레포트만 조회할 수 있습니다.");
		}

		Integer planTrainIndex = findIndexByPlanTrainId(id);

		return PlanTrainResponse.of(planTrainIndex, planTrain);
	}

	private User findUserByUid(String uid) {
		return userRepository.findByUid(uid)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
	}

	private void existsByUserId(Long userId) {
		if (planRepository.existsByUserId(userId)) {
			throw new PlanAlreadyExistsException("해당 사용자는 이미 플랜이 존재합니다.");
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
		for (ContentRequest.Plan.PlanTrain planTrain : planTrains) {
			totalDays++;
			totalTimes += planTrain.sessionTime() != null ? planTrain.sessionTime() : 0;
			totalDistance += planTrain.sessionDistance() != null ? planTrain.sessionDistance() : 0;
		}

		return Plan.builder()
			.user(userInfo)
			.expiredAt(LocalDate.parse(planTrains.getLast().trainDate()))
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
}
