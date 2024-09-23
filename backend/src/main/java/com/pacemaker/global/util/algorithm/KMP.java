package com.pacemaker.global.util.algorithm;

public class KMP {
	public static int getStartIndex(String pattern, String str) {
		int result = -1;

		int patternLen = pattern.length();
		int pi[] = new int[patternLen];

		System.out.println("KMP Start");
		System.out.println("pattern: " + pattern);
		System.out.println("patternLen = " + patternLen);

		int j = 0;
		for (int i = 1; i < patternLen; i++) {
			while (j > 0 && pattern.charAt(j) != pattern.charAt(i)) {
				j = pi[j - 1];
			}

			if (pattern.charAt(j) == pattern.charAt(i)) {
				pi[i] = ++j;
			}
		}

		j = 0;
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			while (j > 0 && str.charAt(i) != pattern.charAt(j)) {
				j = pi[j - 1];
			}

			if (str.charAt(i) == pattern.charAt(j)) {
				if (++j == patternLen) {
					result = i - patternLen + 2;
					break;
				}
			}
		}

		return result;
	}
}
