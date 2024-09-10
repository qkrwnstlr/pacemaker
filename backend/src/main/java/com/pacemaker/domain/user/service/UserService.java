package com.pacemaker.domain.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pacemaker.domain.coach.dto.CoachUpdateRequest;
import com.pacemaker.domain.coach.repository.CoachRepository;
import com.pacemaker.domain.user.dto.CheckUidResponse;
import com.pacemaker.domain.coach.dto.CoachNumberResponse;
import com.pacemaker.domain.user.dto.UserCreateRequest;
import com.pacemaker.domain.user.dto.UserInfoResponse;
import com.pacemaker.domain.user.dto.UserRequest;
import com.pacemaker.domain.user.dto.UserUpdateRequest;
import com.pacemaker.domain.user.entity.Gender;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.user.repository.UserRepository;
import com.pacemaker.global.exception.ConflictException;
import com.pacemaker.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final CoachRepository coachRepository;

	@Transactional
	public void create(UserCreateRequest userCreateRequest) {
		// TODO: VDOT 계산
		if (userRepository.existsByUid(userCreateRequest.uid())) {
			throw new ConflictException("이미 존재하는 사용자입니다.");
		}

		Gender gender = getGender(userCreateRequest.gender());
		userRepository.save(User.builder()
			.uid(userCreateRequest.uid())
			.username(userCreateRequest.name())
			.year(userCreateRequest.year())
			.gender(gender)
			.height(userCreateRequest.height())
			.weight(userCreateRequest.weight())
			.vdot(0)
			.build()
		);
	}

	@Transactional(readOnly = true)
	public CheckUidResponse checkUid(UserRequest userRequest) {
		boolean isAlreadyExists = userRepository.existsByUid(userRequest.uid());
		return new CheckUidResponse(isAlreadyExists);
	}

	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(String uid) {
		User user = findUserByUid(uid);
		Float trainDistance = convertMetersToKilometers(user.getTrainDistance());
		Long coachNumber = getCoachId(user);

		return UserInfoResponse.builder()
			.name(user.getUsername())
			.year(user.getYear())
			.height(user.getHeight())
			.weight(user.getWeight())
			.trainCount(user.getTrainCount())
			.trainTime(user.getTrainTime())
			.trainDistance(trainDistance)
			.coachNumber(coachNumber)
			.build();
	}

	@Transactional
	public UserInfoResponse updateUserInfo(UserUpdateRequest userUpdateRequest) {
		User user = findUserByUid(userUpdateRequest.uid());
		user.update(userUpdateRequest.year(), userUpdateRequest.height(), userUpdateRequest.weight());

		Float trainDistance = convertMetersToKilometers(user.getTrainDistance());
		Long coachNumber = getCoachId(user);

		return UserInfoResponse.builder()
			.name(user.getUsername())
			.year(user.getYear())
			.height(user.getHeight())
			.weight(user.getWeight())
			.trainCount(user.getTrainCount())
			.trainTime(user.getTrainTime())
			.trainDistance(trainDistance)
			.coachNumber(coachNumber)
			.build();
	}

	@Transactional(readOnly = true)
	public CoachNumberResponse getCoachNumber(String uid) {
		User user = findUserByUid(uid);
		return CoachNumberResponse.builder()
			.coachNumber(getCoachId(user))
			.build();
	}

	@Transactional
	public void updateCoachNumber(String uid, CoachUpdateRequest coachUpdateRequest) {
		User user = findUserByUid(uid);
		Coach coach = findCoachById(coachUpdateRequest.coachNumber());

		user.updateCoach(coach);
	}

	@Transactional
	public void deleteUser(UserRequest userRequest) {
		User user = findUserByUid(userRequest.uid());
		userRepository.delete(user);
	}

	private User findUserByUid(String uid) {
		return userRepository.findByUid(uid)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
	}

	private Coach findCoachById(Long coachId) {
		return coachRepository.findById(coachId)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 코치입니다."));
	}

	private Gender getGender(Integer gender) {
		if (gender == 0) {
			return Gender.FEMALE;
		}
		return Gender.MALE;
	}

	private Long getCoachId(User user) {
		return Optional.ofNullable(user.getCoach())
			.map(Coach::getId)
			.orElse(null);
	}

	private Float convertMetersToKilometers(Integer trainDistance) {
		return Math.round(trainDistance / 10.0f) / 100.0f;
	}
}
