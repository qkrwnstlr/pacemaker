package com.pacemaker.domain.report.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.coach.repository.CoachRepository;
import com.pacemaker.domain.plan.entity.PlanTrain;
import com.pacemaker.domain.plan.entity.TrainStatus;
import com.pacemaker.domain.plan.repository.PlanTrainRepository;
import com.pacemaker.domain.report.dto.PlanTrainResponse;
import com.pacemaker.domain.report.dto.ReportFreeRequest;
import com.pacemaker.domain.report.dto.ReportPlanCreateRequest;
import com.pacemaker.domain.report.dto.ReportPlanCreateResponse;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final UserRepository userRepository;
	private final CoachRepository coachRepository;
	private final PlanTrainRepository planTrainRepository;
	private final ObjectMapper objectMapper;
	private final ReportPlanTrainRepository reportPlanTrainRepository;

	@Transactional
	public void createFree(ReportFreeRequest reportFreeRequest) {
		/*
		reportFreeRequestDto의 uid로 User 객체 조회
		return -> Optionanl<User>
		값이 없는 경우 null 대신 Optional.empty()를 반환
		 */
		User user = userRepository.findByUid(reportFreeRequest.uid())
			.orElseThrow(() -> new NotFoundException("해당 사용자가 없습니다."));

		TrainResult tr = reportFreeRequest.trainResult();

		// Report 엔티티 생성 및 저장
		// Report report = Report.builder()
		// 	.user(user)
		// 	.trainDate(LocalDateTime.now()) // 오늘
		// 	.trainDistance(tr.totalDistance())
		// 	.trainTime(tr.totalTime())
		// 	.heartRate(tr.meanHeartRate())
		// 	.pace(tr.meanPace())
		// 	.cadence(tr.meanCadence())
		// 	.kcal(tr.totalKcal())
		// 	.heartZone(tr.heartRateZone().toString())
		// 	.trainType(TrainType.FREE).build();
		//
		// reportRepository.save(report);

	}

	@Transactional
	public ReportPlanCreateResponse createReportPlan(ReportPlanCreateRequest reportPlanCreateRequest) throws
		JsonProcessingException {
		User user = findUserByUid(reportPlanCreateRequest.uid());
		PlanTrain planTrain = findPlanTrainByid(reportPlanCreateRequest.planTrainId());
		Coach coach = findCoachById(reportPlanCreateRequest.coachNumber());
		planTrain.updatePlanTrainStatus(TrainStatus.DONE);

		// TODO: trainEvaluation을 계산하는 LLM 프롬프팅 필요
		String stringTrainEvaluation = "{\"paceEvaluation\":75,\"heartRateEvaluation\":70,\"cadenceEvaluation\":85}";

		String heartZone = convertStringHeartZone(reportPlanCreateRequest.trainResult().heartZone());
		String splitData = convertStringSplitData(reportPlanCreateRequest.trainResult().splitData());
		String trainMap = convertStringTrainMap(reportPlanCreateRequest.trainResult().trainMap());
		String coachMessage = convertStringCoachMessage(reportPlanCreateRequest.trainResult().coachMessage());

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
			.coachMessage(coachMessage)
			.build());

		Integer planTrainIndex = findIndexByPlanTrainId(reportPlanCreateRequest.planTrainId());
		PlanTrainResponse planTrainResponse = PlanTrainResponse.of(planTrainIndex, planTrain);
		TrainResult trainResult = TrainResult.of(report, reportPlanCreateRequest.trainResult().coachMessage());
		TrainEvaluation trainEvaluation = objectMapper.readValue(stringTrainEvaluation, TrainEvaluation.class);
		TrainReport trainReport = TrainReport.builder()
			.trainDate(calculateTrainDuration(reportPlanCreateRequest.trainDate(),
				reportPlanCreateRequest.trainResult().trainTime()))
			.trainResult(trainResult)
			.trainEvaluation(trainEvaluation)
			.build();

		return ReportPlanCreateResponse.builder()
			.planTrain(planTrainResponse)
			.trainReport(trainReport)
			.build();
	}

	private User findUserByUid(String uid) {
		return userRepository.findByUid(uid)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
	}

	private PlanTrain findPlanTrainByid(Long id) {
		return planTrainRepository.findPlanTrainById(id)
			.orElseThrow(() -> new NotFoundException("해당 훈련을 찾을 수 없습니다."));
	}

	private Coach findCoachById(Long coachId) {
		return coachRepository.findById(coachId)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 코치입니다."));
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

	private List<LocalTime> calculateTrainDuration(LocalDateTime endDateTiem, Integer trainTime) {
		LocalTime endTime = endDateTiem.toLocalTime();
		LocalTime startTime = endTime.minusSeconds(trainTime);
		return List.of(startTime, endTime);
	}
}
