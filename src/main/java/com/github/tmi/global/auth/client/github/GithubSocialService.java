package com.github.tmi.global.auth.client.github;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.tmi.global.auth.client.github.dto.GithubAccessTokenResponse;
import com.github.tmi.global.auth.client.github.dto.GithubEmailResponse;
import com.github.tmi.global.auth.client.github.dto.GithubUserResponse;
import com.github.tmi.global.auth.dto.MemberSocialLoginRequest;
import com.github.tmi.global.auth.dto.MemberSocialLoginResponse;
import com.github.tmi.global.auth.exception.OAuthErrorCode;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.member.domain.enums.SocialType;
import com.github.tmi.member.service.SocialService;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubSocialService implements SocialService {

	@Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.registration.github.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.github.client-secret}")
	private String clientSecret;

	private final GithubApiClient githubApiClient;
	private final GithubAuthApiClient githubAuthApiClient;

	@Override
	public MemberSocialLoginResponse login(
		final String authorizationCode,
		final MemberSocialLoginRequest loginRequest
	) {
		String rawAccessToken = getOAuth2AccessToken(authorizationCode);
		String authorization = "Bearer " + rawAccessToken;

		GithubUserResponse userInfo = getUserInfo(authorization);

		return getLoginDto(loginRequest.socialType(), userInfo, authorization, rawAccessToken);
	}

	private String getOAuth2AccessToken(final String authorizationCode) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("code", authorizationCode);
		body.add("redirect_uri", redirectUri);

		GithubAccessTokenResponse response;
		try {
			response = githubAuthApiClient.getOAuth2AccessToken(body);
		} catch (FeignException e) {
			log.error("Failed to get GitHub access token. Error: {}", e.contentUTF8(), e);
			throw new TMIException(OAuthErrorCode.O_AUTH_TOKEN_ERROR);
		}
		return response.accessToken();
	}

	private GithubUserResponse getUserInfo(final String accessToken) {
		log.info("Fetching user info from GitHub API using access token");

		GithubUserResponse response;
		try {
			response = githubApiClient.getUserInformation(accessToken);
			log.info("Successfully retrieved GitHub user info: ID = {}", response.id());
		} catch (FeignException e) {
			log.error("Failed to retrieve user info from GitHub API. Error: {}", e.contentUTF8(), e);
			throw new TMIException(OAuthErrorCode.GET_INFO_ERROR);
		}
		return response;
	}

	private MemberSocialLoginResponse getLoginDto(
		final SocialType socialType,
		final GithubUserResponse userInfo,
		final String authorization,
		final String rawAccessToken
	) {
		return MemberSocialLoginResponse.of(
			String.valueOf(userInfo.id()),
			socialType,
			resolveEmail(userInfo, authorization),
			resolveName(userInfo),
			userInfo.avatarUrl(),
			rawAccessToken,
			userInfo.login()
		);
	}

	private String resolveEmail(final GithubUserResponse userInfo, final String accessToken) {
		if (userInfo.email() != null) {
			return userInfo.email();
		}

		List<GithubEmailResponse> emails;
		try {
			emails = githubApiClient.getUserEmails(accessToken);
		} catch (FeignException e) {
			log.error("Failed to retrieve user emails from GitHub API. Error: {}", e.contentUTF8(), e);
			throw new TMIException(OAuthErrorCode.GET_INFO_ERROR);
		}

		return emails.stream()
			.filter(email -> email.primary() && email.verified())
			.map(GithubEmailResponse::email)
			.findFirst()
			.orElseThrow(() -> new TMIException(OAuthErrorCode.GET_INFO_ERROR));
	}

	private String resolveName(final GithubUserResponse userInfo) {
		return userInfo.name() != null ? userInfo.name() : userInfo.login();
	}
}
