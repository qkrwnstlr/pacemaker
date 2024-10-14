package com.pacemaker.global.util.mattermost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
@ConfigurationProperties("notification.mattermost")
@Primary
public class MattermostProperties {

	private String channel;

	private String pretext;

	private String color = "#ff5d52";

	private String authorName;

	private String authorIcon;

	private String title;

	private String text;

	private String footer;

	public String getFooter() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}
}
