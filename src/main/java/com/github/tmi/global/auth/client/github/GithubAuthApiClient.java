package com.github.tmi.global.auth.client.github;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.tmi.global.auth.client.github.dto.GithubAccessTokenResponse;

@FeignClient(name = "github-auth-client", url = "https://github.com")
public interface GithubAuthApiClient {

	@PostMapping(
		value = "/login/oauth/access_token",
		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	GithubAccessTokenResponse getOAuth2AccessToken(
		@RequestHeader("Accept") String accept,
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("code") String code,
		@RequestParam("redirect_uri") String redirectUri
	);
}