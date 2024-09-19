package com.pacemaker.global.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("PaceMaker API")
			.version("1.0")
			.description("pacemaker api description");

		Server server = new Server();
		server.setUrl("/");
		server.setDescription("Production Server");

		return new OpenAPI()
			.components(new Components())
			.info(info)
			.servers(Collections.singletonList(server));
	}
}