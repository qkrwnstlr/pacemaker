package com.pacemaker.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pacemaker.domain.coach.dto.CoachNumberResponse;
import com.pacemaker.domain.coach.dto.CoachUpdateRequest;
import com.pacemaker.domain.coach.repository.CoachRepository;
import com.pacemaker.domain.user.dto.GoogleLoginRequest;
import com.pacemaker.domain.user.dto.GoogleLoginResponse;
import com.pacemaker.domain.user.dto.UserInfoResponse;
import com.pacemaker.domain.user.dto.UserUpdateRequest;
import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.user.entity.Gender;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.user.repository.UserRepository;
import com.pacemaker.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final CoachRepository coachRepository;

	@Transactional
	public GoogleLoginResponse googleLogin(String uid, GoogleLoginRequest googleLoginRequest) {
		return userRepository.findByUid(uid)
			.map(user -> GoogleLoginResponse.of(user, true))
			.orElseGet(() -> {
				User newUser = User.builder()
					.uid(uid)
					.username(googleLoginRequest.name())
					.build();
				return GoogleLoginResponse.of(userRepository.save(newUser), false);
			});
	}

	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(String uid) {
		return UserInfoResponse.of(findUserByUid(uid));
	}

	@Transactional
	public UserInfoResponse updateUserInfo(String uid, UserUpdateRequest userUpdateRequest) {
		User user = findUserByUid(uid);

		Gender gender = getGender(userUpdateRequest.gender());
		String injuries = convertStringInjuries(userUpdateRequest.injuries());

		user.update(userUpdateRequest.name(), userUpdateRequest.age(), userUpdateRequest.height(),
			userUpdateRequest.weight(), gender, injuries);

		return UserInfoResponse.of(user);
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
	public void deleteUser(String uid) {
		User user = findUserByUid(uid);
		userRepository.delete(user);
	}

	@Transactional(readOnly = true)
	public Object findMonthlyCalenderByUid(String uid, Integer year, Integer month) {
		User findUser = findUserByUid(uid);

		// year와 month로 between해서 그 사이 모든 레포트 플랜 등등 가져와야함

		return null;
	}

	private User findUserByUid(String uid) {
		return userRepository.findByUid(uid)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
	}

	private Gender getGender(String gender) {
		if (gender.equals("MALE")) {
			return Gender.MALE;
		}
		if (gender.equals("FEMALE")) {
			return Gender.FEMALE;
		}
		return Gender.UNKNOWN;
	}

	private String convertStringInjuries(List<String> injuries) {
		return injuries.toString();
	}

	private Long getCoachId(User user) {
		return Optional.ofNullable(user.getCoach())
			.map(Coach::getId)
			.orElse(null);
	}

	private Coach findCoachById(Long coachId) {
		return coachRepository.findById(coachId)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 코치입니다."));
	}
}
