package com.github.tmi.member.service;

import com.github.tmi.global.auth.dto.MemberSocialLoginRequest;
import com.github.tmi.global.auth.dto.MemberSocialLoginResponse;

public interface SocialService {

	MemberSocialLoginResponse login(
		final String authorizationCode,
		final MemberSocialLoginRequest loginRequest
	);
}
