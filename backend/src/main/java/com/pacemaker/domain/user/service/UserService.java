package com.pacemaker.domain.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pacemaker.domain.user.dto.CheckUidResponse;
import com.pacemaker.domain.user.dto.UserCreateRequest;
import com.pacemaker.domain.user.dto.UserInfoResponse;
import com.pacemaker.domain.user.dto.UserRequest;
import com.pacemaker.domain.user.dto.UserUpdateRequest;
import com.pacemaker.domain.user.entity.Gender;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.user.repository.UserRepository;
import com.pacemaker.global.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	@Autowired
	private final UserRepository userRepository;

	@Transactional
	public void create(UserCreateRequest userCreateRequest) {
		// TODO: VDOT 계산
		// TODO: 동일한 uid 있을 경우 생성 불가
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

	private User findUserByUid(String uid) {
		return userRepository.findByUid(uid)
			.orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
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
