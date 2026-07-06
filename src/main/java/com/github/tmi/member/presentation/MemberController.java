package com.github.tmi.member.presentation;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.tmi.global.auth.annotation.CurrentMember;
import com.github.tmi.global.auth.client.github.dto.GithubRepositoryResponse;
import com.github.tmi.global.auth.dto.MemberSocialLoginRequest;
import com.github.tmi.global.response.dto.SuccessResponse;
import com.github.tmi.github.service.GithubRepositoryService;
import com.github.tmi.member.dto.DashboardResponse;
import com.github.tmi.member.dto.LoginSuccessResponse;
import com.github.tmi.member.dto.MemberInfoResponse;
import com.github.tmi.member.dto.TokenReissueResponse;
import com.github.tmi.member.exception.MemberSuccessCode;
import com.github.tmi.member.service.MemberLoginService;
import com.github.tmi.member.service.MemberService;
import com.github.tmi.member.service.MemberTokenService;
import com.github.tmi.portfolio.service.PortfolioService;

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
	private final GithubRepositoryService githubRepositoryService;
	private final PortfolioService portfolioService;

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

	@GetMapping("/me/repositories")
	public ResponseEntity<SuccessResponse<List<GithubRepositoryResponse>>> getMyRepositories(
		@CurrentMember final Long memberId
	) {
		List<GithubRepositoryResponse> repositories = githubRepositoryService.getMyRepositories(memberId);
		return ResponseEntity.ok(
			SuccessResponse.of(MemberSuccessCode.GET_REPOSITORIES_SUCCESS, repositories));
	}

	@GetMapping("/me/dashboard")
	public ResponseEntity<SuccessResponse<DashboardResponse>> getMyDashboard(
		@CurrentMember final Long memberId
	) {
		DashboardResponse response = portfolioService.getDashboard(memberId);
		return ResponseEntity.ok(SuccessResponse.of(MemberSuccessCode.GET_DASHBOARD_SUCCESS, response));
	}

	@PostMapping("/reissue")
	public ResponseEntity<SuccessResponse<TokenReissueResponse>> reissue(
		@CookieValue(value = REFRESH_TOKEN, required = false) final String refreshToken
	) {
		String accessToken = memberTokenService.reissueAccessToken(refreshToken);
		return ResponseEntity.ok(
			SuccessResponse.of(MemberSuccessCode.REISSUE_SUCCESS, TokenReissueResponse.of(accessToken)));
	}
}
