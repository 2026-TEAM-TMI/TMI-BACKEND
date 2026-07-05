package com.github.tmi.file.exception;

import org.springframework.http.HttpStatus;

import com.github.tmi.global.response.base.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileSuccessCode implements BaseCode {

	/*
	200 OK
	 */
	CREATE_PRESIGNED_URL_SUCCESS(HttpStatus.OK, "presigned URL 발급 성공"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
