package com.pacemaker.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${openai.api.key}")
	private String openAIApiKey;

	@Value("${notification.mattermost.webhook-url}")
	private String webhookUrl;

	@Value("${gpu.server.url}")
	private String gpuServerUrl;

	@Bean
	public WebClient openAIWebClient() {
		return WebClient.builder()
			.baseUrl("https://api.openai.com/v1")
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAIApiKey)
			.build();
	}

	@Bean
	public WebClient mattermostWebClient() {
		return WebClient.builder()
			.baseUrl(webhookUrl)
			.build();
	}

	@Bean
	public WebClient gpuServerWebClient() {
		return WebClient.builder()
			.baseUrl(gpuServerUrl)
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024))  // 10MB로 설정
			.build();
	}
}
