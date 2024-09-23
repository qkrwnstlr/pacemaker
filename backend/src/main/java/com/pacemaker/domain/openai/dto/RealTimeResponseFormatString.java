package com.pacemaker.domain.openai.dto;

import lombok.Getter;

@Getter
public class RealTimeResponseFormatString {

	public static String responseFormat = """
		{
		  "name": "realtimecoach",
		  "strict": false,
		  "schema": {
		    "type": "object",
		    "required": [
		      "textFeedBack",
		      "textCheer"
		    ],
		    "properties": {
		      "textCheer": {
		        "type": "string",
		        "description": "Cheer-up message within 2 sentences in Korean"
		      },
		      "textFeedback": {
		        "type": "string",
		        "description": "Feedback message of the about the user's run within 2 sentences in Korean"
		      }
		    }
		  }
		}
		""";
}
