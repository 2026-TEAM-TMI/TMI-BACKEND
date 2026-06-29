package com.github.tmi.global.auth.dto;

import com.github.tmi.member.domain.enums.SocialType;

public record MemberSocialLoginRequest(
	SocialType socialType
) {
}
