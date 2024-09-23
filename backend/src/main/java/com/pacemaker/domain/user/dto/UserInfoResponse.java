package com.pacemaker.domain.user.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pacemaker.domain.coach.entity.Coach;
import com.pacemaker.domain.user.entity.Gender;
import com.pacemaker.domain.user.entity.User;

import lombok.Builder;

@Builder
public record UserInfoResponse(String name, Integer age, Integer height, Integer weight, String gender,
							   List<String> injuries, Integer trainCount, Integer trainTime, Float trainDistance,
							   Long coachNumber) {
	public static UserInfoResponse of(User user) {
		return UserInfoResponse.builder()
			.name(user.getUsername())
			.age(user.getAge())
			.height(user.getHeight())
			.weight(user.getWeight())
			.gender(convertStringGender(user.getGender()))
			.injuries(convertListInjuries(user.getInjuries()))
			.trainCount(user.getTrainCount())
			.trainTime(user.getTrainTime())
			.trainDistance(convertMetersToKilometers(user.getTrainDistance()))
			.coachNumber(getCoachNumber(user))
			.build();
	}

	private static String convertStringGender(Gender gender) {
		if (gender == Gender.MALE) {
			return "MALE";
		}
		if (gender == Gender.FEMALE) {
			return "FEMALE";
		}
		return "UNKNOWN";
	}

	private static List<String> convertListInjuries(String injuries) {
		if (injuries == null || injuries.equals("[]")) {
			return List.of();
		}

		String content = injuries.substring(1, injuries.length() - 1);
		return Arrays.stream(content.split(", "))
			.map(String::trim)
			.collect(Collectors.toList());
	}

	private static Float convertMetersToKilometers(Integer trainDistance) {
		return Math.round(trainDistance / 10.0f) / 100.0f;
	}

	private static Long getCoachNumber(User user) {
		return Optional.ofNullable(user.getCoach())
			.map(Coach::getId)
			.orElse(null);
	}
}
