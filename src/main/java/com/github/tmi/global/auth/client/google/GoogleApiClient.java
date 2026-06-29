package com.github.tmi.global.auth.client.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.github.tmi.global.auth.client.google.dto.GoogleUserResponse;

@FeignClient(name = "google-api-client", url = "https://www.googleapis.com/oauth2/v3")
public interface GoogleApiClient {

	@GetMapping("/userinfo")
	GoogleUserResponse getUserInformation(@RequestHeader("Authorization") String authorization);
}
