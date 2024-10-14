package com.pacemaker.global.util.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class RootContainer {
	Root root;
}
