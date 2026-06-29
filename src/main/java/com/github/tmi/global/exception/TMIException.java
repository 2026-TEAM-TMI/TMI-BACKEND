package com.github.tmi.global.exception;

import com.github.tmi.global.response.base.BaseCode;

import lombok.Getter;

@Getter
public class TMIException extends RuntimeException {
	private final BaseCode baseCode;

	public TMIException(BaseCode baseCode) {
		super(baseCode.getMessage());
		this.baseCode = baseCode;
	}
}
