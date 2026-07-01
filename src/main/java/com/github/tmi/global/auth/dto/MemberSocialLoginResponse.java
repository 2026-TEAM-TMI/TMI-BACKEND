package com.github.tmi.global.auth.dto;

import com.github.tmi.member.domain.enums.SocialType;

public record MemberSocialLoginResponse(
	String socialId,
	SocialType socialType,
	String email,
	String name,
	String profileImage,
	String accessToken,
	String socialLogin
) {
	public static MemberSocialLoginResponse of(
		final String socialId,
		final SocialType socialType,
		final String email,
		final String name
	) {
		return new MemberSocialLoginResponse(socialId, socialType, email, name, null, null, null);
	}

	public static MemberSocialLoginResponse of(
		final String socialId,
		final SocialType socialType,
		final String email,
		final String name,
		final String profileImage,
		final String accessToken,
		final String socialLogin
	) {
		return new MemberSocialLoginResponse(socialId, socialType, email, name, profileImage, accessToken, socialLogin);
	}
}
