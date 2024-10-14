package com.pacemaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케쥴링 설정
public class PacemakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacemakerApplication.class, args);
	}

}
