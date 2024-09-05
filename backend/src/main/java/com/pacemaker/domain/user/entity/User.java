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

	@Column(nullable = false, length = 20)
	private String username;

	@Column(nullable = false)
	private Integer year;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	private Integer height;

	@Column(nullable = false)
	private Integer weight;

	@Column(name = "train_count", nullable = false, columnDefinition = "int default 0")
	private Integer trainCount;

	@Column(name = "train_time", nullable = false, columnDefinition = "int default 0")
	private Integer trainTime;

	@Column(name = "train_distance", nullable = false, columnDefinition = "int default 0")
	private Integer trainDistance;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer vdot;

	@Builder
	public User(Coach coach, String uid, String username, Integer year, Gender gender, Integer height, Integer weight,
		Integer trainCount, Integer trainTime, Integer trainDistance, LocalDateTime createdAt, Integer vdot) {
		this.coach = coach;
		this.uid = uid;
		this.username = username;
		this.year = year;
		this.gender = gender;
		this.height = height;
		this.weight = weight;
		this.trainCount = (trainCount != null) ? trainCount : 0;
		this.trainTime = (trainTime != null) ? trainTime : 0;
		this.trainDistance = (trainDistance != null) ? trainDistance : 0;
		this.createdAt = createdAt;
		this.vdot = vdot;
	}
}
