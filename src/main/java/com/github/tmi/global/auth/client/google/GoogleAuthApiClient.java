package com.github.tmi.global.auth.client.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.tmi.global.auth.client.google.dto.GoogleAccessTokenResponse;

@FeignClient(name = "google-auth-client", url = "https://oauth2.googleapis.com")
public interface GoogleAuthApiClient {

	// client_secret 등 민감값이 URL 쿼리에 노출되지 않도록 form-urlencoded body로 전송
	@PostMapping(
		value = "/token",
		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	GoogleAccessTokenResponse getOAuth2AccessToken(@RequestBody MultiValueMap<String, String> body);
}
