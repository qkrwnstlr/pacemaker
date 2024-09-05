package com.pacemaker.domain.train.entity;

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
public class Train {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name_eng", nullable = false)
	private String nameEng;

	@Column(name = "name_kor", nullable = false)
	private String nameKor;

	@Column(nullable = false)
	private String description;

	@Builder
	public Train(String nameEng, String nameKor, String description) {
		this.nameEng = nameEng;
		this.nameKor = nameKor;
		this.description = description;
	}
}
