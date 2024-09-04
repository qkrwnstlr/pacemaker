package com.pacemaker.domain.user.entity;

import java.time.LocalDateTime;

import com.pacemaker.domain.coach.entity.Coach;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
	private int id;
	@ManyToOne(fetch = FetchType.LAZY)
	private Coach coach;
	private String uid;
	private String username;
	private Integer year;
	private String gender;
	private Integer height;
	private Integer weight;
	private Integer trainCount;
	private Integer trainTime;
	private Integer trainDistance;
	private LocalDateTime createdAt;
	private Integer vdot;

	@Builder
	public User(Coach coach, String uid, String username, Integer year, String gender, Integer height, Integer weight,
		Integer trainCount, Integer trainTime, Integer trainDistance, LocalDateTime createdAt, Integer vdot) {
		this.coach = coach;
		this.uid = uid;
		this.username = username;
		this.year = year;
		this.gender = gender;
		this.height = height;
		this.weight = weight;
		this.trainCount = trainCount;
		this.trainTime = trainTime;
		this.trainDistance = trainDistance;
		this.createdAt = createdAt;
		this.vdot = vdot;
	}
}
