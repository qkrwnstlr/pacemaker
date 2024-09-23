package com.pacemaker.domain.report.entity;

import java.time.LocalDateTime;

import com.pacemaker.domain.user.entity.User;

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

@Entity
@Getter
@NoArgsConstructor
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "train_date", nullable = false, columnDefinition = "datetime default now()")
	private LocalDateTime trainDate;

	@Column(name = "train_distance", nullable = false)
	private Integer trainDistance;

	@Column(name = "train_time", nullable = false)
	private Integer trainTime;

	@Column(name = "heart_rate")
	private Integer heartRate;

	@Column(nullable = false)
	private Integer pace;

	@Column(nullable = false)
	private Integer cadence;

	@Column(nullable = false)
	private Integer kcal;

	@Lob
	@Column(name = "heart_zone", columnDefinition = "TEXT")
	private String heartZone;

	@Lob
	@Column(name = "split_data", columnDefinition = "TEXT")
	private String splitData;

	@Lob
	@Column(name = "train_map", columnDefinition = "TEXT")
	private String trainMap;

	@Column(name = "report_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	@Builder
	public Report(User user, LocalDateTime trainDate, Integer trainDistance, Integer trainTime, Integer heartRate,
		Integer pace, Integer cadence, Integer kcal, String heartZone, String splitData, String trainMap, ReportType reportType) {
		this.user = user;
		this.trainDate = trainDate;
		this.trainDistance = trainDistance;
		this.trainTime = trainTime;
		this.heartRate = heartRate;
		this.pace = pace;
		this.cadence = cadence;
		this.kcal = kcal;
		this.heartZone = heartZone;
		this.splitData = splitData;
		this.trainMap = trainMap;
		this.reportType = reportType;
	}
}
