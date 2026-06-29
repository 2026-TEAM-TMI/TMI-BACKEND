package com.github.tmi.global.response.base;

import org.springframework.http.HttpStatus;

public interface BaseCode {
	HttpStatus getHttpStatus();

	String getMessage();
}
