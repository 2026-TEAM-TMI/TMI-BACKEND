package com.github.tmi.member.dto;

import com.github.tmi.member.domain.enums.Role;

public record MemberInfoResponse(
	Long id,
	String githubLogin,
	String name,
	String profileImage,
	Role role,
	String email
) {
	public static MemberInfoResponse of(
		Long id,
		final String githubLogin,
		final String name,
		final String profileImage,
		final Role role,
		final String email
	) {
		return new MemberInfoResponse(id, githubLogin, name, profileImage, role, email);
	}
}
