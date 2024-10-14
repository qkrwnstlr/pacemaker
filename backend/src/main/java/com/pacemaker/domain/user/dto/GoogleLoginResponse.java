package com.pacemaker.domain.user.dto;

import com.pacemaker.domain.user.entity.User;

import lombok.Builder;

@Builder
public record GoogleLoginResponse(UserInfoResponse userInfoResponse, Boolean isAlreadyExists) {
	public static GoogleLoginResponse of(User user, Boolean isAlreadyExists) {
		return GoogleLoginResponse.builder()
			.userInfoResponse(UserInfoResponse.of(user))
			.isAlreadyExists(isAlreadyExists)
			.build();
	}
}
