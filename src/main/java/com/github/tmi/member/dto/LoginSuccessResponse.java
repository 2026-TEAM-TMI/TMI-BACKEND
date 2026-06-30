package com.github.tmi.member.dto;

import com.github.tmi.member.domain.enums.Role;

public record LoginSuccessResponse(
	String accessToken,
	String refreshToken,
	Role role
) {
	public static LoginSuccessResponse of(
		final String accessToken,
		final String refreshToken,
		final Role role
	) {
		return new LoginSuccessResponse(accessToken, refreshToken, role);
	}
}