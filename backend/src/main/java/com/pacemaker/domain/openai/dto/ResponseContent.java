package com.pacemaker.domain.openai.dto;

public record ResponseContent(String message, ResponseContent context, Plan plan) {
}
