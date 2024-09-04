package com.pacemaker.domain.coach.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Coach {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(name = "image_url")
	private String imageUrl;

	@Builder
	public Coach(String name, String description, String imageUrl) {
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
	}
}
