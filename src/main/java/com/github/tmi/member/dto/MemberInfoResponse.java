package com.github.tmi.member.dto;

import com.github.tmi.member.domain.enums.Role;

public record MemberInfoResponse(
	String githubLogin,
	String name,
	String profileImage,
	Role role,
	String email
) {
	public static MemberInfoResponse of(
		final String githubLogin,
		final String name,
		final String profileImage,
		final Role role,
		final String email
	) {
		return new MemberInfoResponse(githubLogin, name, profileImage, role, email);
	}
}
