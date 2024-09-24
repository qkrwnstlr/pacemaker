package com.pacemaker.domain.openai.dto;

import lombok.Getter;

@Getter
public class ResponseFormatString {
	// public static String responseFormat = """
	// 	{ type: json_schema, json_schema: { type: object, properties: { message: { type: string, description: GPT의 응답 메시지 }, context: { type: object, properties: { goal: { type: string, description: 사용자의 목표 (예: 살을 빼고 싶어, 마라톤에 참여하고 싶어) }, goalTime: { type: integer, description: 목표 시간 }, goalDistance: { type: integer, description: 목표 거리 }, trainDayOfWeek: { type: array, description: 원하는 요일 }, userInfo: { type: object, properties: { age: { type: integer, description: 사용자의 나이 }, height: { type: integer, description: 사용자의 키 }, weight: { type: integer, description: 사용자의 몸무게 }, gender: { type: string, description: 사용자의 성별 }, injuries: { type: string, description: 과거 부상 이력 } }, required: [age, height, weight, gender, injuries] } }, required: [goal, goalTime, goalDistance, userInfo] }, plan: { type: object, properties: { totalDays: { type: integer, description: 총 훈련 일수 }, totalTimes: { type: integer, description: 총 훈련 시간 }, trainDetails: { type: array, items: { type: object, properties: { index: { type: integer, description: 훈련 인덱스 }, date: { type: string, format: date(yyyy-MM-dd), description: 훈련 날짜 }, pace: { type: integer, description: 훈련 페이스 (1km 분 당 단위) }, trainDistance: { type: integer, description: 총 훈련 거리 (미터 단위) }, trainDuration: { type: integer, description: 총 훈련 시간 (초 단위) (trainParam + (repeat * interParam)) }, trainParam: { type: integer, description: 페이스 러닝 시간 (초 단위) }, repeat: { type: integer, description: 페이스 러닝 반복 횟수 }, interParam: { type: integer, description: 페이스 러닝 사이 조깅 시간 (초 단위) } } }, description: 훈련 상세 정보 } }, required: [totalDays, totalTimes, trainDetails] } }, required: [message, context] } }
	// 	""";

	// public static String responseFormat = """
	// 	{"type": "json_schema","json_schema": {"type": "object","properties": {"message": {"type": "string","description": "GPT의 응답 메시지"},"context": {"type": "object","properties": {"goal": {"type": "string","description": "사용자의 목표 (예: 살을 빼고 싶어, 마라톤에 참여하고 싶어)"},"goalTime": {"type": "integer","description": "목표 시간"},"goalDistance": {"type": "integer","description": "목표 거리"},"trainDayOfWeek": {"type": "array","description": "원하는 요일"},"userInfo": {"type": "object","properties": {"age": {"type": "integer","description": "사용자의 나이"},"height": {"type": "integer","description": "사용자의 키"},"weight": {"type": "integer","description": "사용자의 몸무게"},"gender": {"type": "integer","description": "사용자의 성별"},"injuries": {"type": "string","description": "과거 부상 이력"}},"required": ["age", "height", "weight", "gender", "injuries"]}},"required": ["goal", "goalTime", "goalDistance", "userInfo"]},"plan": {"type": "object","properties": {"totalDays": {"type": "integer","description": "총 훈련 일수"},"totalTimes": {"type": "integer","description": "총 훈련 시간"},"trainDetails": {"type": "array","items": {"type": "object","properties": {"index": {"type": "integer","description": "훈련 인덱스"},"date": {"type": "string","format": "date-time","description": "훈련 날짜"},"pace": {"type": "integer","description": "훈련 페이스 (minute/km 단위)"},"trainDistance": {"type": "integer","description": "훈련 거리 (미터 단위)"},"trainDuration": {"type": "integer","description": "훈련 시간 (초 단위)"},"trainParam": {"type": "integer","description": "반복 훈련의 러닝 시간 (초 단위)"},"repeat": {"type": "integer","description": "반복 횟수"},"interParam": {"type": "integer","description": "반복 중간 조깅 시간 (초 단위)"}}},"description": "훈련 상세 정보"}},"required": ["totalDays", "totalTimes", "trainDetails"]}},"required": ["message", "context"]}}
	// 	""";

	public static String responseFormat = """
		{
		  "name": "pacemaker",
		  "strict": false,
		  "schema": {
		    "type": "object",
		    "properties": {
		      "message": {
		        "type": "string",
		        "description": "response message of the GPT"
		      },
		      "context": {
		        "type": "object",
		        "properties": {
		          "goal": {
		            "type": "string",
		            "description": "user's running goal (ex. lose weight, running a marathon)"
		          },
		          "goalTime": {
		            "type": "integer",
		            "description": "goal duration of the goal run"
		          },
		          "goalDistance": {
		            "type": "integer",
		            "description": "goal distance"
		          },
		          "trainDayOfWeek": {
		            "type": "array",
		            "items": {
		              "type": "string",
		              "enum": [
		                "Monday",
		                "Tuesday",
		                "Wednesday",
		                "Thursday",
		                "Friday",
		                "Saturday",
		                "Sunday"
		              ],
		              "description": "day of the week"
		            },
		            "maxItems": 3,
		            "description": "days of the week for the train"
		          },
		          "userInfo": {
		            "type": "object",
		            "properties": {
		              "age": {
		                "type": "integer",
		                "description": "user's age"
		              },
		              "height": {
		                "type": "integer",
		                "description": "user's height (cm)"
		              },
		              "weight": {
		                "type": "integer",
		                "description": "user's weight (kg)"
		              },
		              "gender": {
		                "type": "string",
		                "enum": [
		                  "MALE",
		                  "FEMALE",
		                  ""
		                ],
		                "description": "user's gender"
		              },
		              "injuries": {
		                "type": "array",
		                "items": {
		                  "type": "string",
		                  "description": "history of all injuries"
		                }
		              },
		              "recentRunPace": {
		                "type": "integer",
		                "description": "running pace of the recent runs (seconds per kilometer)"
		              },
		              "recentRunDistance": {
		                "type": "integer",
		                "description": "running distance of the recent runs"
		              },
		              "recentRunHeartRate": {
		                "type": "integer",
		                "description": "running average heart rate of the recent runs"
		              }
		            },
		            "required": [
		              "age",
		              "height",
		              "weight",
		              "gender",
		              "injuries",
		              "recentRunPace",
		              "recentRunDistance",
		              "recentRunHeartRate"
		            ]
		          }
		        },
		        "required": [
		          "goal",
		          "goalTime",
		          "goalDistance",
		          "trainDayOfWeek",
		          "userInfo"
		        ]
		      },
		      "plan": {
		        "type": "object",
		        "properties": {
		          "planTrains": {
		            "type": "array",
		            "items": {
		              "type": "object",
		              "properties": {
		                "index": {
		                  "type": "integer",
		                  "description": "index"
		                },
		                "trainDate": {
		                  "type": "date",
		                  "format": "yyyy-MM-dd",
		                  "description": "train date"
		                },
		                "paramType": {
		                  "type": "string",
		                  "enum": [
		                    "time",
		                    "distance"
		                  ],
		                  "description": "type "
		                },
		                "repeat": {
		                  "type": "integer",
		                  "description": "number of repeat of the main train"
		                },
		                "trainParam": {
		                  "type": "integer",
		                  "description": "time duration or distance of the main(repeat) train in seconds"
		                },
		                "trainPace": {
		                  "type": "integer",
		                  "description": "train pace in sec/km"
		                },
		                "interParam": {
		                  "type": "integer",
		                  "description": "time duration or distance of recovery jog in between in repeat of the main train"
		                }
		              },
		              "required": [
		                "index",
		                "trainDate",
		                "paramType",
		                "repeat",
		                "trainParam",
		                "trainPace",
		                "interParam"
		              ]
		            }
		          }
		        },
		        "required": [
		          "planTrains"
		        ]
		      }
		    },
		    "required": [
		      "message",
		      "context",
		      "plan"
		    ]
		  }
		}
		""";
}
