package com.pacemaker.domain.plan.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pacemaker.domain.user.entity.User;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Plan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "created_at", nullable = false)
	private LocalDate createdAt;

	@Column(name = "expired_at", nullable = false)
	private LocalDate expiredAt;

	@Column(name = "total_days", nullable = false)
	private Integer totalDays;

	@Column(name = "total_times", nullable = false)
	private Integer totalTimes;

	@Column(name = "total_distances", nullable = false)
	private Integer totalDistances;

	@Lob
	@Column(nullable = false, columnDefinition = "TEXT")
	private String context;

	@Column(name = "completed_count", nullable = false, columnDefinition = "int default 0")
	private Integer completedCount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PlanStatus status;

	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlanTrain> planTrains = new ArrayList<>();

	@PrePersist
	protected void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now.toLocalDate();
	}

	@Builder
	public Plan(User user, LocalDate createdAt, LocalDate expiredAt, Integer totalDays, Integer totalTimes,
		Integer totalDistances, String context, Integer completedCount, PlanStatus status) {
		this.user = user;
		this.createdAt = createdAt;
		this.expiredAt = expiredAt;
		this.totalDays = totalDays;
		this.totalTimes = totalTimes;
		this.totalDistances = totalDistances;
		this.context = context;
		this.completedCount = (completedCount != null) ? completedCount : 0;
		this.status = (status != null) ? status : PlanStatus.ACTIVE;
	}

	// 연관관계 편의 메서드
	public void addPlanTrain(PlanTrain planTrain) {
		planTrains.add(planTrain);
		planTrain.setPlan(this);
	}

	public void removePlanTrain(PlanTrain planTrain) {
		planTrains.remove(planTrain);
	}

	public void removeBeforePlanTrains() {
		this.planTrains.removeIf(planTrain -> planTrain.getStatus() == PlanTrainStatus.BEFORE);
	}

	public void updateCompletedCount() {
		this.completedCount += 1;
	}

	public void updatePlanStatus(PlanStatus status) {
		this.status = status;
	}

	public void updatePlanTrainReport(Integer totalDistances, Integer totalTimes) {
		this.totalDistances = totalDistances;
		this.totalTimes = totalTimes;
	}

	public void updatePlanDetails(Integer totalDays, Integer totalTimes, Integer totalDistances, LocalDate createdAt,
		LocalDate expiredAt) {
		this.totalDays = totalDays;
		this.totalTimes = totalTimes;
		this.totalDistances = totalDistances;
		this.createdAt = createdAt;
		this.expiredAt = expiredAt;
	}

	public void updatePlanExpiredAt(LocalDate expiredAt) {
		this.expiredAt = expiredAt;
	}

	public void updatePlanCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
}
