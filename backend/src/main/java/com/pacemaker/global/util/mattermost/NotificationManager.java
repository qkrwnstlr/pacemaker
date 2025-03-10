package com.pacemaker.global.util.mattermost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationManager {

	private Logger log = LoggerFactory.getLogger(NotificationManager.class);
	private final MattermostSender mmSender;

	public void sendNotification(Exception e, String uri, String method, String params) {
		log.info("#### SEND Notification");
		mmSender.sendMessage(e, uri, method, params);
	}
}
