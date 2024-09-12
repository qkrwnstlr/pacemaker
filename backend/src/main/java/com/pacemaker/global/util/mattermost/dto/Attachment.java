package com.pacemaker.global.util.mattermost.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Attachment {
	private String channel;

	private String pretext;

	private String color;

	@SerializedName("author_name")
	private String authorName;

	@SerializedName("author_icon")
	private String authorIcon;

	private String title;

	private String text;

	private String footer;

	private StringBuilder sb;

	public void addExceptionInfo(Exception e) {
		this.title = e.getClass().getSimpleName();
		sb = new StringBuilder();

		sb.append("**Error Message**").append('\n').append('\n').append("```").append(e.getMessage()).append("```")
			.append('\n').append('\n');

		this.text = sb.toString();
	}

	public void addExceptionInfo(Exception e, String uri) {
		this.addExceptionInfo(e);
		sb = new StringBuilder(text);

		sb.append("**Reqeust URL**").append('\n').append('\n').append(uri).append('\n').append('\n');

		this.text = sb.toString();
	}

	public void addExceptionInfo(Exception e, String uri, String params) {
		this.addExceptionInfo(e, uri);
		sb = new StringBuilder(text);

		sb.append("**Parameters**").append('\n').append('\n').append(params).append('\n').append('\n');

		this.text = sb.toString();
	}
}
