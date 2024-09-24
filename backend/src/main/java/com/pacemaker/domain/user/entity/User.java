package com.pacemaker.domain.user.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.pacemaker.domain.coach.entity.Coach;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coach_id")
	private Coach coach;

	@Column(nullable = false)
	private String uid;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer age;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer height;

	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer weight;

	@Lob
	@Column(nullable = false, columnDefinition = "TEXT")
	private String injuries;

	@Column(name = "train_count", nullable = false, columnDefinition = "int default 0")
	private Integer trainCount;

	@Column(name = "train_time", nullable = false, columnDefinition = "int default 0")
	private Integer trainTime;

	@Column(name = "train_distance", nullable = false, columnDefinition = "int default 0")
	private Integer trainDistance;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Builder
	public User(String uid, String username, Integer age, Gender gender, Integer height, Integer weight,
		String injuries, Integer trainCount, Integer trainTime, Integer trainDistance) {
		this.uid = uid;
		this.username = username;
		this.age = (age != null) ? age : 0;
		this.gender = (gender != null) ? gender : Gender.UNKNOWN;
		this.height = (height != null) ? height : 0;
		this.weight = (weight != null) ? weight : 0;
		this.injuries = (injuries != null) ? injuries : "[]";
		this.trainCount = (trainCount != null) ? trainCount : 0;
		this.trainTime = (trainTime != null) ? trainTime : 0;
		this.trainDistance = (trainDistance != null) ? trainDistance : 0;
	}

	public void update(String username, Integer age, Integer height, Integer weight, Gender gender, String injuries) {
		this.username = username;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.gender = gender;
		this.injuries = injuries;
	}

	public void updateCoach(Coach coach) {
		this.coach = coach;
	}

	public void update(String username, Integer age, Integer height, Integer weight, String gender, String injuries) {
		this.username = username;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.gender = stringToGender(gender);
		this.injuries = injuries;
	}

	public Gender stringToGender(String gender) {
		if ("MALE".equals(gender)) {
			return Gender.MALE;
		}

		if ("FEMALE".equals(gender)) {
			return Gender.FEMALE;
		}

		return Gender.UNKNOWN;
	}

	public String genderToString(Gender gender) {
		if (gender == Gender.MALE) {
			return "MALE";
		}

		if (gender == Gender.FEMALE) {
			return "FEMALE";
		}

		return "UNKNOWN";
	}
}
