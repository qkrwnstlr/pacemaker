package com.pacemaker.domain.user.dto;

import lombok.Builder;

@Builder
public record UserInfoResponse(String name, Integer year, Integer height, Integer weight, Integer trainCount,
							   Integer trainTime, Float trainDistance, Long coachNumber) {
}
