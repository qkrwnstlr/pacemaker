package com.pacemaker.global.util.mattermost.dto;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Props {
	private String card;

	public Props(Exception e) {
		StringBuilder sb = new StringBuilder();
		StringWriter sw = new StringWriter();

		e.printStackTrace(new PrintWriter(sw));
		sb.append("**Stack Trace**").append("\n").append('\n').append("```");
		sb.append(sw.toString().substring(0, Math.min(5500, sw.toString().length())) + "\n...")
			.append('\n')
			.append('\n');

		this.card = sb.toString();
	}
}
