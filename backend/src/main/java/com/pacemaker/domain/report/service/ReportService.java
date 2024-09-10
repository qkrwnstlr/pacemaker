package com.pacemaker.domain.report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pacemaker.domain.report.dto.ReportFreeRequestDto;
import com.pacemaker.domain.report.dto.TrainResultDto;
import com.pacemaker.domain.report.entity.Report;
import com.pacemaker.domain.report.entity.TrainType;
import com.pacemaker.domain.report.repository.ReportRepository;
import com.pacemaker.domain.train.entity.Train;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.user.repository.UserRepository;
import com.pacemaker.global.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createFree(ReportFreeRequestDto reportFreeRequestDto) {
		/*
		reportFreeRequestDto의 uid로 User 객체 조회
		return -> Optionanl<User>
		값이 없는 경우 null 대신 Optional.empty()를 반환
		 */
		User user = userRepository.findByUid(reportFreeRequestDto.uid())
			.orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다."));

		TrainResultDto tr = reportFreeRequestDto.trainResult();

		// Report 엔티티 생성 및 저장
		Report report = Report.builder()
			.user(user)
			.trainDate(LocalDateTime.now()) // 오늘
			.trainDistance(tr.totalDistance())
			.trainTime(tr.totalTime())
			.heartRate(tr.meanHeartRate())
			.pace(tr.meanPace())
			.cadence(tr.meanCadence())
			.kcal(tr.totalKcal())
			.heartZone(tr.heartRateZone().toString())
			.trainType(TrainType.FREE).build();

		reportRepository.save(report);

	}
}
