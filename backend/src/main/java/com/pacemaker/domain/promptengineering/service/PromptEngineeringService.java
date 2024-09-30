package com.pacemaker.domain.promptengineering.service;

import org.springframework.stereotype.Service;

import com.pacemaker.domain.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromptEngineeringService {

	private final OpenAiService openAiService;

	public void test() {

	}
}
