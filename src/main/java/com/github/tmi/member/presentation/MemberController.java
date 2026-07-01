package com.github.tmi.member.presentation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.tmi.global.auth.annotation.CurrentMember;
import com.github.tmi.global.auth.dto.MemberSocialLoginRequest;
import com.github.tmi.global.response.dto.SuccessResponse;
import com.github.tmi.member.dto.LoginSuccessResponse;
import com.github.tmi.member.dto.MemberInfoResponse;
import com.github.tmi.member.exception.MemberSuccessCode;
import com.github.tmi.member.service.MemberLoginService;
import com.github.tmi.member.service.MemberService;
import com.github.tmi.member.service.MemberTokenService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private static final String REFRESH_TOKEN = "refreshToken";
	private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

	private final MemberLoginService memberLoginService;
	private final MemberTokenService memberTokenService;
	private final MemberService memberService;

	@PostMapping("/login")
	public ResponseEntity<SuccessResponse<LoginSuccessResponse>> login(
		@RequestParam("authorizationCode") String authorizationCode,
		@RequestBody MemberSocialLoginRequest storeSocialLoginRequest,
		HttpServletResponse httpServletResponse
	) {
		LoginSuccessResponse successResponse = memberLoginService.login(authorizationCode, storeSocialLoginRequest);
		ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, successResponse.refreshToken())
			.maxAge(COOKIE_MAX_AGE)
			.path("/")
			.secure(true)
			.sameSite("None")
			.httpOnly(true)
			.build();


		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.body(SuccessResponse.of(MemberSuccessCode.LOGIN_SUCCESS, successResponse));
	}

	@GetMapping("/me")
	public ResponseEntity<SuccessResponse<MemberInfoResponse>> getMyInfo(
		@CurrentMember final Long memberId
	) {
		MemberInfoResponse response = memberService.getMyInfo(memberId);
		return ResponseEntity.ok(SuccessResponse.of(MemberSuccessCode.GET_MY_INFO_SUCCESS, response));
	}
}
