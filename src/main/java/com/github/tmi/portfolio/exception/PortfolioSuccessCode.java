package com.github.tmi.portfolio.exception;

import org.springframework.http.HttpStatus;

import com.github.tmi.global.response.base.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortfolioSuccessCode implements BaseCode {

	/*
	200 OK
	 */
	GENERATE_SUCCESS(HttpStatus.OK, "포트폴리오 생성 성공"),
	GET_PORTFOLIOS_SUCCESS(HttpStatus.OK, "포트폴리오 목록 조회 성공"),
	GET_FEED_SUCCESS(HttpStatus.OK, "포트폴리오 피드 조회 성공");

	private final HttpStatus httpStatus;
	private final String message;
}
