package com.pacemaker.global.util.mattermost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.pacemaker.global.util.mattermost.dto.Attachment;
import com.pacemaker.global.util.mattermost.dto.Attachments;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MattermostSender {

	private Logger log = LoggerFactory.getLogger(MattermostSender.class);

	@Value("${notification.mattermost.enabled}")
	private boolean mmEnabled;

	private final WebClient mattermostWebClient;
	private final MattermostProperties mmProperties;

	public void sendMessage(Exception exception, String uri, String params) {
		if (!mmEnabled) {
			return;
		}

		try {
			Attachment attachment = Attachment.builder()
				.channel(mmProperties.getChannel())
				.authorIcon(mmProperties.getAuthorIcon())
				.authorName(mmProperties.getAuthorName())
				.color(mmProperties.getColor())
				.pretext(mmProperties.getPretext())
				.title(mmProperties.getTitle())
				.text(mmProperties.getText())
				.footer(mmProperties.getFooter())
				.build();

			attachment.addExceptionInfo(exception, uri, params);
			Attachments attachments = new Attachments(attachment);
			attachments.addProps(exception);
			String payload = new Gson().toJson(attachments);

			HttpHeaders headers = new HttpHeaders();
			// headers.set("Content-type", MediaType.APPLICATION_JSON_VALUE);
			headers.setContentType(MediaType.APPLICATION_JSON);

			mattermostWebClient.post()
				.bodyValue(payload)
				.headers(httpHeaders -> httpHeaders.addAll(headers))
				.retrieve()
				.bodyToMono(String.class)
				.block();

		} catch (Exception e) {
			log.error("#### ERROR!! Notification Manager : {}", e.getMessage());
		}
	}
}
