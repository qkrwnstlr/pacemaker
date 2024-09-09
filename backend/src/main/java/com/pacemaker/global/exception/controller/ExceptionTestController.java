package com.pacemaker.global.exception.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class ExceptionTestController {

	@GetMapping("/1")
	public ResponseEntity<?> test() throws Exception {
		throw new Exception("1 -> Exception");
	}

	@GetMapping("/notnumber")
	public ResponseEntity<?> test2() throws Exception {
		throw new NumberFormatException("notnumber -> NumberFormatException");
	}

}

