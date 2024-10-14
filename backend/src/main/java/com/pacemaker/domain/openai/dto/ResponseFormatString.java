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

	public static String planChatResponseFormat = """
		{
		    "type": "json_schema",
		    "json_schema": {
		      "name": "pacemaker",
		      "strict": true,
		      "schema": {
		        "additionalProperties": false,
		        "type": "object",
		        "properties": {
		          "message": { "type": "string", "description": "response message of the GPT" },
		          "context": {
		            "additionalProperties": false,
		            "type": "object",
		            "properties": {
		              "goal": {
		                "type": "string",
		                "description": "user's running goal (ex. lose weight, running a marathon)"
		              },
		              "goalTime": { "type": "integer", "description": "goal time in seconds" },
		              "goalDistance": { "type": "integer", "description": "goal distance" },
		              "trainDayOfWeek": {
		                "type": "array",
		                "items": {
		                  "type": "string",
		                  "enum": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
		                  "description": "day of the week"
		                },
		                "description": "days of the week for the train"
		              },
		              "userInfo": {
		                "type": "object",
		                "additionalProperties": false,
		                "properties": {
		                  "age": { "type": "integer", "description": "user's age" },
		                  "height": { "type": "integer", "description": "user's height (cm)" },
		                  "weight": { "type": "integer", "description": "user's weight (kg)" },
		                  "gender": { "type": "string", "enum": ["MALE", "FEMALE", ""], "description": "user's gender" },
		                  "injuries": {
		                    "type": "array",
		                    "items": { "type": "string", "description": "history of all injuries" }
		                  },
		                  "recentRunPace": {
		                    "type": "integer",
		                    "description": "running pace of the recent runs (seconds per kilometer), -1 if none"
		                  },
		                  "recentRunDistance": {
		                    "type": "integer",
		                    "description": "running distance of the recent runs, -1 if none"
		                  },
		                  "recentRunHeartRate": {
		                    "type": "integer",
		                    "description": "running average heart rate of the recent runs, -1 if none"
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
		            "required": ["goal", "goalTime", "goalDistance", "trainDayOfWeek", "userInfo"]
		          },
		          "plan": {
		            "description": "plan of the total train sessions, the size of all the arrays within the objects must be same",
		            "type": "object",
		            "required": ["index", "trainDate", "paramType", "repetition", "trainParam", "trainPace", "interParam"],
		            "properties": {
		              "index": {
		                "description": "indexs of each train session",
		                "type": "array",
		                "items": {
		                  "description": "index",
		                  "type": "integer"
		                }
		              },
		              "trainDate": {
		                "description": "trainDates of each train session",
		                "type": "array",
		                "items": {
		                  "description": "trainDate in YYYY-mm-dd format",
		                  "type": "string"
		                }
		              },
		              "paramType": {
		                "description": "parameter types of each train session",
		                "type": "array",
		                "items": {
		                  "description": "parameter types of each train session, the array size must correspond with the array size of index object.",
		                  "type": "string",
		                  "enum": ["distance"]
		                }
		              },
		              "repetition": {
		                "description": "number of repetition of the main train of each train session",
		                "type": "array",
		                "items": {
		                  "description": "number of the repetition of the main train, integer 1 to 6",
		                  "type": "integer"
		                }
		              },
		              "trainParam": {
		                "description": "parameters of the main train of each train session",
		                "type": "array",
		                "items": {
		                  "description": "distance of the main(repetition) train in meters",
		                  "type": "integer"
		                }
		              },
		              "trainPace": {
		                "description": "train pace of the main train of each train session",
		                "type": "array",
		                "items": {
		                  "description": "pace of the main train in sec/km",
		                  "type": "integer"
		                }
		              },
		              "interParam": {
		                "description": "parameters of recovery jog in of each train session",
		                "type": "array",
		                "items": {
		                  "description": "distance of recovery jog in between the repetition of the main train",
		                  "type": "integer"
		                }
		              }
		            },
		            "additionalProperties": false
		          }
		        },
		        "required": ["message", "context", "plan"]
		      }
		    }
		  }
		""";

	public static String realTimeResponseFormat = """
		{
		   "type": "json_schema",
		   "json_schema": {
		     "name": "realtimecoach",
		     "strict": true,
		     "schema": {
		       "additionalProperties": false,
		       "type": "object",
		       "required": ["textFeedback", "textCheer"],
		       "properties": {
		         "textCheer": {
		           "type": "string",
		           "description": "Cheer-up message within 2 sentences in Korean"
		         },
		         "textFeedback": {
		           "type": "string",
		           "description": "Feedback message about the user's run within 1 sentence in Korean"
		         }
		       }
		     }
		   }
		}""";

	public static String dailyCreateChatResponseFormat = """
		{
		   "type": "json_schema",
		   "json_schema": {
		     "name": "pacemaker",
		     "strict": true,
		     "schema": {
		       "additionalProperties": false,
		       "type": "object",
		       "properties": {
		         "message": {
		           "type": "string",
		           "description": "response message of the GPT"
		         },
		         "context": {
		           "additionalProperties": false,
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
		             "userInfo": {
		               "type": "object",
		               "additionalProperties": false,
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
		                   "enum": ["MALE", "FEMALE", ""],
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
		           "required": ["goal", "goalTime", "goalDistance", "userInfo"]
		         },
		         "planTrain": {
		           "type": "object",
		           "additionalProperties": false,
		           "properties": {
		             "index": {
		               "type": "integer",
		               "description": "index"
		             },
		             "trainDate": {
		               "type": "string",
		               "description": "train date"
		             },
		             "paramType": {
		               "type": "string",
		               "enum": ["distance"]
		             },
		             "repetition": {
		               "type": "integer",
		               "description": "number of repetition of the main train"
		             },
		             "trainParam": {
		               "type": "integer",
		               "description": "distance of the main(repetition) train inor meters"
		             },
		             "trainPace": {
		               "type": "integer",
		               "description": "train pace in sec/km"
		             },
		             "interParam": {
		               "type": "integer",
		               "description": "distance of recovery jog in between in repetition of the main train"
		             }
		           },
		           "required": ["index", "trainDate", "paramType", "repetition", "trainParam", "trainPace", "interParam"]
		         }
		       },
		       "required": ["message", "context", "planTrain"]
		     }
		   }
		}""";

	public static String createTrainEvaluationResponseFormat = """
		{
		  "type": "json_schema",
		  "json_schema": {
		    "name": "pacemaker",
		    "strict": true,
		    "schema": {
		      "type": "object",
		      "required": ["trainEvaluation", "coachMessage"],
		      "additionalProperties": false,
		      "properties": {
		        "trainEvaluation": {
		          "type": "object",
		          "required": ["paceEvaluation", "heartRateEvaluation", "cadenceEvaluation"],
		          "additionalProperties": false,
		          "properties": {
		            "paceEvaluation": {
		              "type": "integer",
		              "description": "Evaluation score for pace (1 to 100)"
		            },
		            "heartRateEvaluation": {
		              "type": "integer",
		              "description": "Evaluation score for heart rate (1 to 100)"
		            },
		            "cadenceEvaluation": {
		              "type": "integer",
		              "description": "Evaluation score for cadence (1 to 100)"
		            }
		          }
		        },
		        "coachMessage": {
		          "type": "array",
		          "items": {
		            "type": "string",
		            "description": "Feedback messages from the coach about the user's run, each message in Korean"
		          },
		          "description": "List of feedback messages about the user's performance"
		        }
		      }
		    }
		  }
		}""";
}
