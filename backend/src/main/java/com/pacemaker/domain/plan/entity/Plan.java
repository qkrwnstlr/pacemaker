package com.pacemaker.domain.plan.entity;

import com.pacemaker.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Plan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "created_at", nullable = false, columnDefinition = "datetime default now()")
	private LocalDateTime createdAt;

	@Column(name = "expired_at", nullable = false, columnDefinition = "datetime")
	private LocalDateTime expiredAt;

	@Column(name = "total_count", nullable = false)
	private Integer totalCount;

	@Column(name = "completed_count", nullable = false)
	private Integer completedCount;

	@Column(name = "day_of_week", nullable = false)
	private Integer dayOfWeek;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PlanStatus status;

	@Column(name = "goal_distance", nullable = false, columnDefinition = "int default 0")
	private Integer goalDistance;

	@Column(name = "goal_time", nullable = false, columnDefinition = "int default 0")
	private Integer goalTime;

	@Builder
	public Plan(User user, LocalDateTime createdAt, LocalDateTime expiredAt, Integer totalCount, Integer completedCount,
		Integer dayOfWeek, PlanStatus status, Integer goalDistance, Integer goalTime) {

		this.user = user;
		this.createdAt = createdAt;
		this.expiredAt = expiredAt;
		this.totalCount = totalCount;
		this.completedCount = completedCount;
		this.dayOfWeek = dayOfWeek;
		this.status = status;
		this.goalDistance = goalDistance;
		this.goalTime = goalTime;
	}
}
