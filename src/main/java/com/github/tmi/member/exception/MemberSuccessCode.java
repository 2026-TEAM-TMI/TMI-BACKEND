package com.github.tmi.member.exception;

import org.springframework.http.HttpStatus;

import com.github.tmi.global.response.base.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberSuccessCode implements BaseCode {

	/*
	200 OK
	 */
	LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
