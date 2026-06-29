package com.github.tmi.member.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.tmi.global.auth.dto.MemberSocialLoginRequest;
import com.github.tmi.global.auth.dto.MemberSocialLoginResponse;
import com.github.tmi.global.auth.exception.OAuthErrorCode;
import com.github.tmi.global.auth.jwt.JwtTokenProvider;
import com.github.tmi.global.auth.security.MemberAuthentication;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.member.domain.Member;
import com.github.tmi.member.domain.enums.SocialType;
import com.github.tmi.member.dto.LoginSuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLoginService {

	private final SocialService googleSocialService;
	private final MemberService memberService;
	private final MemberTokenService memberTokenService;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public LoginSuccessResponse login(
		final String authorizationCode,
		final MemberSocialLoginRequest request
	) {
		// 1. 소셜 서비스로부터 사용자 정보 조회
		MemberSocialLoginResponse socialInfo = findSocialInfo(authorizationCode, request);

		// 2. 기존 회원을 찾거나, 없으면 새로 가입
		Member member = findOrRegisterMember(socialInfo);

		// 3. 토큰 발급 후 로그인 응답 반환
		return issueTokens(member);
	}

	// 소셜 타입에 따라 사용자 정보 조회
	private MemberSocialLoginResponse findSocialInfo(
		final String authorizationCode,
		final MemberSocialLoginRequest request
	) {
		SocialService socialService = getSocialService(request.socialType());
		return socialService.login(authorizationCode, request);
	}

	// 해당하는 소셜 서비스 반환
	private SocialService getSocialService(final SocialType socialType) {
		return switch (socialType) {
			case GOOGLE -> googleSocialService;
			// TODO: GITHUB 소셜 로그인 구현체 추가 시 case 등록
			default -> throw new TMIException(OAuthErrorCode.SOCIAL_TYPE_NOT_SUPPORTED);
		};
	}

	// 기존 회원을 찾거나, 없으면 새로 가입
	private Member findOrRegisterMember(final MemberSocialLoginResponse socialInfo) {
		if (memberService.checkMemberExists(socialInfo.socialId(), socialInfo.socialType())) {
			return memberService.findMember(socialInfo.socialId(), socialInfo.socialType());
		}
		return memberService.registerMember(socialInfo);
	}

	// 액세스/리프레시 토큰 발급 및 리프레시 토큰 저장
	private LoginSuccessResponse issueTokens(final Member member) {
		Authentication authentication = new MemberAuthentication(
			member.getId(),
			null,
			List.of(member.getRole().toGrantedAuthority())
		);

		String accessToken = jwtTokenProvider.issueAccessToken(authentication);
		String refreshToken = jwtTokenProvider.issueRefreshToken(authentication);

		memberTokenService.saveRefreshToken(member.getId(), refreshToken);

		return LoginSuccessResponse.of(accessToken, refreshToken, member.getRole());
	}
}
