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
	GET_MY_INFO_SUCCESS(HttpStatus.OK, "내 정보 조회 성공"),
	GET_REPOSITORIES_SUCCESS(HttpStatus.OK, "GitHub 레포지토리 목록 조회 성공"),
	REISSUE_SUCCESS(HttpStatus.OK, "액세스 토큰 재발급 성공"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
