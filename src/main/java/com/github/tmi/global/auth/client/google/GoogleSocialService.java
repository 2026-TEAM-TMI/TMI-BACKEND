package com.github.tmi.global.auth.client.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.tmi.global.auth.client.google.dto.GoogleAccessTokenResponse;
import com.github.tmi.global.auth.client.google.dto.GoogleUserResponse;
import com.github.tmi.global.auth.dto.MemberSocialLoginRequest;
import com.github.tmi.global.auth.dto.MemberSocialLoginResponse;
import com.github.tmi.global.auth.exception.OAuthErrorCode;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.member.domain.enums.SocialType;
import com.github.tmi.member.service.SocialService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleSocialService implements SocialService {

	private static final String AUTH_CODE = "authorization_code";

	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;

	private final GoogleApiClient googleApiClient;
	private final GoogleAuthApiClient googleAuthApiClient;

	@Override
	public MemberSocialLoginResponse login(
		final String authorizationCode,
		final MemberSocialLoginRequest loginRequest
	) {
		String accessToken;
		try {
			accessToken = getOAuth2Authentication(authorizationCode);
		} catch (FeignException e) {
			throw new TMIException(OAuthErrorCode.O_AUTH_TOKEN_ERROR);
		}

		return getLoginDto(loginRequest.socialType(), getUserInfo(accessToken));
	}

	private String getOAuth2Authentication(
		final String authorizationCode) {
		GoogleAccessTokenResponse response;
		try {
			response = googleAuthApiClient.getOAuth2AccessToken(
				AUTH_CODE,
				clientId,
				clientSecret,
				redirectUri,
				authorizationCode
			);
		} catch (FeignException e) {
			throw new TMIException(OAuthErrorCode.O_AUTH_TOKEN_ERROR);
		}
		return "Bearer " + response.accessToken();
	}

	private GoogleUserResponse getUserInfo(
		final String accessToken
	) {
		log.info("Fetching user info from Google API using access token");

		GoogleUserResponse response;
		try {
			response = googleApiClient.getUserInformation(accessToken);
			log.info("Successfully retrieved user info: ID = {}", response.sub());
		} catch (FeignException e) {
			log.error("Failed to retrieve user info from Google API. Error: {}", e.contentUTF8(), e);
			throw new TMIException(OAuthErrorCode.GET_INFO_ERROR);
		}
		return response;
	}

	private MemberSocialLoginResponse getLoginDto(
		final SocialType socialType,
		final GoogleUserResponse googleUserResponse
	) {
		return MemberSocialLoginResponse.of(
			googleUserResponse.sub(),
			socialType,
			googleUserResponse.email(),
			googleUserResponse.name()
		);
	}
}
