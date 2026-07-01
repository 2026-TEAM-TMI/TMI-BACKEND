package com.github.tmi.global.auth.client.github;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.github.tmi.global.auth.client.github.dto.GithubEmailResponse;
import com.github.tmi.global.auth.client.github.dto.GithubUserResponse;

@FeignClient(name = "github-api-client", url = "https://api.github.com")
public interface GithubApiClient {

	@GetMapping("/user")
	GithubUserResponse getUserInformation(@RequestHeader("Authorization") String authorization);

	@GetMapping("/user/emails")
	List<GithubEmailResponse> getUserEmails(@RequestHeader("Authorization") String authorization);
}
