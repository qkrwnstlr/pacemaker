package com.pacemaker.domain.report.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.coach.repository.CoachRepository;
import com.pacemaker.domain.openai.service.OpenAiService;
import com.pacemaker.domain.plan.entity.Plan;
import com.pacemaker.domain.plan.entity.PlanStatus;
import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.entity.TrainStatus;
import com.pacemaker.domain.plan.repository.PlanRepository;
import com.pacemaker.domain.plan.repository.PlanTrainRepository;
import com.pacemaker.domain.report.dto.CreateTrainEvaluationRequest;
import com.pacemaker.domain.report.dto.CreateTrainEvaluationResponse;
import com.pacemaker.domain.report.dto.PlanTrainResponse;
import com.pacemaker.domain.report.dto.ReportFreeCreateRequest;
import com.pacemaker.domain.report.dto.ReportFreeResponse;
import com.pacemaker.domain.report.dto.ReportPlanCreateRequest;
import com.pacemaker.domain.report.dto.ReportPlanResponse;
import com.pacemaker.domain.report.dto.SplitData;
import com.pacemaker.domain.report.dto.TrainEvaluation;
import com.pacemaker.domain.report.dto.TrainReport;
import com.pacemaker.domain.report.dto.TrainResult;
import com.pacemaker.domain.report.entity.Report;
import com.pacemaker.domain.report.entity.ReportPlanTrain;
import com.pacemaker.domain.report.entity.ReportType;
import com.pacemaker.domain.report.repository.ReportPlanTrainRepository;
import com.pacemaker.domain.report.repository.ReportRepository;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.user.repository.UserRepository;
import com.pacemaker.global.exception.NotFoundException;
import com.pacemaker.global.exception.UserMismatchException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final OpenAiService openAiService;
	private final ReportRepository reportRepository;
	private final UserRepository userRepository;
	private final CoachRepository coachRepository;
	private final PlanTrainRepository planTrainRepository;
	private final ObjectMapper objectMapper;
	private final ReportPlanTrainRepository reportPlanTrainRepository;
	private final PlanRepository planRepository;

	@Transactional
	public ReportPlanResponse createReportPlan(ReportPlanCreateRequest reportPlanCreateRequest) throws
		JsonProcessingException {
		// TODO: 플랜 자동 수정 필요

		User user = findUserByUid(reportPlanCreateRequest.uid());
		PlanTrain planTrain = findPlanTrainById(reportPlanCreateRequest.planTrainId());
		Plan plan = planTrain.getPlan();
		Coach coach = findCoachById(reportPlanCreateRequest.coachNumber());

		planTrain.updatePlanTrainStatus(TrainStatus.DONE);
		user.updateUserTrainReport(reportPlanCreateRequest.trainResult().trainTime(),
			reportPlanCreateRequest.trainResult().trainDistance());
		plan.updateCompletedCount();

		if (Objects.equals(plan.getTotalDays(), plan.getCompletedCount())) {
			plan.updatePlanStatus(PlanStatus.COMPLETED);
			plan.updatePlanTrainReport(planRepository.findSumPlanDistanceByPlanId(plan.getId()),
				planRepository.findSumPlanTimeByPlanId(plan.getId()));
		}

		CreateTrainEvaluationRequest createTrainEvaluationRequest = new CreateTrainEvaluationRequest(
			planTrain.getTrainPace(), reportPlanCreateRequest.trainResult().splitData(),
			reportPlanCreateRequest.trainResult().heartZone(), coach.getDescription());
		String stringCreateTrainEvaluationResponse = openAiService.createTrainEvaluation(createTrainEvaluationRequest)
			.block();
		CreateTrainEvaluationResponse createTrainEvaluationResponse = new Gson().fromJson(
			stringCreateTrainEvaluationResponse, CreateTrainEvaluationResponse.class);

		TrainEvaluation trainEvaluation = createTrainEvaluationResponse.trainEvaluation();
		String stringTrainEvaluation = convertStringTrainEvaluation(trainEvaluation);
		List<String> coachMessage = createTrainEvaluationResponse.coachMessage();

		String heartZone = convertStringHeartZone(reportPlanCreateRequest.trainResult().heartZone());
		String splitData = convertStringSplitData(reportPlanCreateRequest.trainResult().splitData());
		String trainMap = convertStringTrainMap(reportPlanCreateRequest.trainResult().trainMap());
		String stringCoachMessage = convertStringCoachMessage(reportPlanCreateRequest.trainResult().coachMessage());

		Report report = reportRepository.save(Report.builder()
			.user(user)
			.trainDate(reportPlanCreateRequest.trainDate())
			.trainDistance(reportPlanCreateRequest.trainResult().trainDistance())
			.trainTime(reportPlanCreateRequest.trainResult().trainTime())
			.heartRate(reportPlanCreateRequest.trainResult().heartRate())
			.pace(reportPlanCreateRequest.trainResult().pace())
			.cadence(reportPlanCreateRequest.trainResult().cadence())
			.kcal(reportPlanCreateRequest.trainResult().kcal())
			.heartZone(heartZone)
			.splitData(splitData)
			.trainMap(trainMap)
			.reportType(ReportType.PLAN)
			.trainEvaluation(stringTrainEvaluation)
			.build());

		reportPlanTrainRepository.save(ReportPlanTrain.builder()
			.report(report)
			.planTrain(planTrain)
			.coach(coach)
			.coachMessage(stringCoachMessage)
			.build());

		Integer planTrainIndex = findIndexByPlanTrainId(reportPlanCreateRequest.planTrainId());
		PlanTrainResponse planTrainResponse = PlanTrainResponse.of(planTrainIndex, planTrain);
		TrainResult trainResult = TrainResult.of(report, reportPlanCreateRequest.coachNumber(), coachMessage);
		TrainReport trainReport = TrainReport.builder()
			.trainDuration(calculateTrainDuration(reportPlanCreateRequest.trainDate(),
				reportPlanCreateRequest.trainResult().trainTime()))
			.trainResult(trainResult)
			.trainEvaluation(trainEvaluation)
			.build();

		return ReportPlanResponse.builder()
			.planTrain(planTrainResponse)
			.trainReport(trainReport)
			.build();
	}

	@Transactional(readOnly = true)
	public ReportPlanResponse findReportPlan(Long reportId, String uid) throws JsonProcessingException {
		User user = findUserByUid(uid);
		Report report = findReportById(reportId);

		if (user != report.getUser()) {
			throw new UserMismatchException("본인의 레포트만 조회할 수 있습니다.");
		}

		ReportPlanTrain reportPlanTrain = findReportPlanTrainById(reportId);
		PlanTrain planTrain = findPlanTrainById(reportPlanTrain.getPlanTrain().getId());

		Integer planTrainIndex = findIndexByPlanTrainId(planTrain.getId());
		PlanTrainResponse planTrainResponse = PlanTrainResponse.of(planTrainIndex, planTrain);
		TrainResult trainResult = TrainResult.of(report, reportPlanTrain.getCoach().getId(),
			convertListCoachMessage(reportPlanTrain.getCoachMessage()));
		TrainEvaluation trainEvaluation = convertStringTrainEvaluation(report.getTrainEvaluation());
		TrainReport trainReport = TrainReport.builder()
			.trainDuration(calculateTrainDuration(report.getTrainDate(), report.getTrainTime()))
			.trainResult(trainResult)
			.trainEvaluation(trainEvaluation)
			.build();

		return ReportPlanResponse.builder()
			.planTrain(planTrainResponse)
			.trainReport(trainReport)
			.build();
	}

	@Transactional
	public ReportFreeResponse createReportFree(ReportFreeCreateRequest reportFreeCreateRequest) throws
		JsonProcessingException {

		User user = findUserByUid(reportFreeCreateRequest.uid());
		user.updateUserTrainReport(reportFreeCreateRequest.trainResult().trainTime(),
			reportFreeCreateRequest.trainResult().trainDistance());

		String heartZone = convertStringHeartZone(reportFreeCreateRequest.trainResult().heartZone());
		String trainMap = convertStringTrainMap(reportFreeCreateRequest.trainResult().trainMap());

		Report report = reportRepository.save(Report.builder()
			.user(user)
			.trainDate(reportFreeCreateRequest.trainDate())
			.trainDistance(reportFreeCreateRequest.trainResult().trainDistance())
			.trainTime(reportFreeCreateRequest.trainResult().trainTime())
			.heartRate(reportFreeCreateRequest.trainResult().heartRate())
			.pace(reportFreeCreateRequest.trainResult().pace())
			.cadence(reportFreeCreateRequest.trainResult().cadence())
			.kcal(reportFreeCreateRequest.trainResult().kcal())
			.heartZone(heartZone)
			.trainMap(trainMap)
			.reportType(ReportType.FREE)
			.build());

		TrainResult trainResult = TrainResult.builder()
			.trainDistance(report.getTrainDistance())
			.trainTime(report.getTrainTime())
			.heartRate(report.getHeartRate())
			.pace(report.getPace())
			.cadence(report.getCadence())
			.kcal(report.getKcal())
			.heartZone(reportFreeCreateRequest.trainResult().heartZone())
			.trainMap(reportFreeCreateRequest.trainResult().trainMap())
			.build();

		List<LocalTime> trainDuration = calculateTrainDuration(reportFreeCreateRequest.trainDate(),
			reportFreeCreateRequest.trainResult().trainTime());
		TrainReport trainReport = TrainReport.builder()
			.trainDuration(trainDuration)
			.trainResult(trainResult)
			.build();

		return ReportFreeResponse.builder()
			.trainReport(trainReport)
			.build();
	}

	@Transactional(readOnly = true)
	public ReportFreeResponse findReportFree(Long reportId, String uid) throws JsonProcessingException {
		User user = findUserByUid(uid);
		Report report = findReportById(reportId);

		if (user != report.getUser()) {
			throw new UserMismatchException("본인의 레포트만 조회할 수 있습니다.");
		}

		List<Integer> heartZone = convertListHeartZone(report.getHeartZone());
		List<List<Double>> trainMap = convertListTrainMap(report.getTrainMap());

		TrainResult trainResult = TrainResult.builder()
			.trainDistance(report.getTrainDistance())
			.trainTime(report.getTrainTime())
			.heartRate(report.getHeartRate())
			.pace(report.getPace())
			.cadence(report.getCadence())
			.kcal(report.getKcal())
			.heartZone(heartZone)
			.trainMap(trainMap)
			.build();

		TrainReport trainReport = TrainReport.builder()
			.trainDuration(calculateTrainDuration(report.getTrainDate(), report.getTrainTime()))
			.trainResult(trainResult)
			.build();

		return ReportFreeResponse.builder()
			.trainReport(trainReport)
			.build();
	}

	private User findUserByUid(String uid) {
		return userRepository.findByUid(uid)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
	}

	private PlanTrain findPlanTrainById(Long id) {
		return planTrainRepository.findPlanTrainById(id)
			.orElseThrow(() -> new NotFoundException("해당 훈련을 찾을 수 없습니다."));
	}

	private Coach findCoachById(Long coachId) {
		return coachRepository.findById(coachId)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 코치입니다."));
	}

	private Report findReportById(Long id) {
		return reportRepository.findReportById(id)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 레포트입니다."));
	}

	private ReportPlanTrain findReportPlanTrainById(Long id) {
		return reportPlanTrainRepository.findReportPlanTrainByReportId(id)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 플랜훈련 레포트입니다. 플랜 훈련을 진행한 이력이 있는지 확인해주세요."));
	}

	private Integer findIndexByPlanTrainId(Long planTrainId) {
		Integer index = planTrainRepository.findIndexByPlanTrainId(planTrainId);

		if (index == -1) {
			throw new NotFoundException("해당 훈련을 찾을 수 없습니다.");
		}

		return index;
	}

	private String convertStringHeartZone(List<Integer> heartZone) throws JsonProcessingException {
		return objectMapper.writeValueAsString(heartZone);
	}

	private String convertStringSplitData(List<SplitData> splitData) throws JsonProcessingException {
		return objectMapper.writeValueAsString(splitData);
	}

	private String convertStringTrainMap(List<List<Double>> trainMap) throws JsonProcessingException {
		return objectMapper.writeValueAsString(trainMap);
	}

	private String convertStringCoachMessage(List<String> coachMessage) throws JsonProcessingException {
		return objectMapper.writeValueAsString(coachMessage);
	}

	private String convertStringTrainEvaluation(TrainEvaluation trainEvaluation) throws JsonProcessingException {
		return objectMapper.writeValueAsString(trainEvaluation);
	}

	private List<String> convertListCoachMessage(String coachMessage) throws JsonProcessingException {
		return objectMapper.readValue(coachMessage, new TypeReference<>() {
		});
	}

	private TrainEvaluation convertStringTrainEvaluation(String trainEvaluation) throws JsonProcessingException {
		return objectMapper.readValue(trainEvaluation, TrainEvaluation.class);
	}

	private List<Integer> convertListHeartZone(String heartZone) throws JsonProcessingException {
		return objectMapper.readValue(heartZone, new TypeReference<>() {
		});
	}

	private List<List<Double>> convertListTrainMap(String trainMap) throws JsonProcessingException {
		return objectMapper.readValue(trainMap, new TypeReference<>() {
		});
	}

	private List<LocalTime> calculateTrainDuration(LocalDateTime endDateTiem, Integer trainTime) {
		LocalTime endTime = endDateTiem.toLocalTime();
		LocalTime startTime = endTime.minusSeconds(trainTime);
		return List.of(startTime, endTime);
	}
}
