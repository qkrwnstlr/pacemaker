package com.pacemaker.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("PaceMaker API")
			.version("1.0")
			.description("pacemaker api description");
		return new OpenAPI()
			.components(new Components())
			.info(info);
	}
}