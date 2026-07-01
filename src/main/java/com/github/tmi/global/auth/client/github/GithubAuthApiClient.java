package com.github.tmi.global.auth.client.github;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.tmi.global.auth.client.github.dto.GithubAccessTokenResponse;

@FeignClient(name = "github-auth-client", url = "https://github.com")
public interface GithubAuthApiClient {

	// client_secret 등 민감값이 URL 쿼리에 노출되지 않도록 form-urlencoded body로 전송
	// Accept: application/json 이 있어야 GitHub이 JSON으로 응답한다(기본은 form-urlencoded)
	@PostMapping(
		value = "/login/oauth/access_token",
		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
		headers = "Accept=application/json")
	GithubAccessTokenResponse getOAuth2AccessToken(@RequestBody MultiValueMap<String, String> body);
}
