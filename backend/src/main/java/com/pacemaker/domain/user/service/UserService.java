package com.pacemaker.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pacemaker.domain.user.dto.CheckUidResponse;
import com.pacemaker.domain.user.dto.UserCreateRequest;
import com.pacemaker.domain.user.dto.UserRequest;
import com.pacemaker.domain.user.entity.Gender;
import com.pacemaker.domain.user.entity.User;
import com.pacemaker.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	@Autowired
	private final UserRepository userRepository;

	@Transactional
	public void create(UserCreateRequest userCreateRequest) {
		// TODO: VDOT 계산
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

	private Gender getGender(Integer gender) {
		if (gender == 0) {
			return Gender.FEMALE;
		}
		return Gender.MALE;
	}
}
