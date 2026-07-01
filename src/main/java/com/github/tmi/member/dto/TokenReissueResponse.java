package com.github.tmi.member.dto;

public record TokenReissueResponse(
	String accessToken
) {
	public static TokenReissueResponse of(final String accessToken) {
		return new TokenReissueResponse(accessToken);
	}
}