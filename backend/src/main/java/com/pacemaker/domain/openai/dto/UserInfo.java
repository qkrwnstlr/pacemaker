package com.pacemaker.domain.openai.dto;

import java.util.List;

record UserInfo(int age, int height, int weight, String gender, List<String> injuries) {
}
