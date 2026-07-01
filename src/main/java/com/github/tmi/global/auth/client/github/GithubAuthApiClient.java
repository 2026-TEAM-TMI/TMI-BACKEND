package com.github.tmi.global.auth.client.github;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.tmi.global.auth.client.github.dto.GithubAccessTokenResponse;

@FeignClient(name = "github-auth-client", url = "https://github.com")
public interface GithubAuthApiClient {

	@PostMapping(value = "/login/oauth/access_token", headers = "Accept=application/json")
	GithubAccessTokenResponse getOAuth2AccessToken(
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("code") String authorizationCode,
		@RequestParam("redirect_uri") String redirectUri
	);
}
