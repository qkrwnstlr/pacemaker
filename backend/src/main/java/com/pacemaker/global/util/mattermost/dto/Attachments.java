package com.pacemaker.global.util.mattermost.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Attachments {
	private Props props;
	private List<Attachment> attachments = new ArrayList<>();

	public Attachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Attachments(Attachment attachment) {
		this.attachments.add(attachment);
	}

	public void addProps(Exception e) {
		props = new Props(e);
	}
}
