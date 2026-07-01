package com.github.tmi.global.auth.client.github.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GithubAccessTokenResponse(
	String accessToken,
	String scope,
	String tokenType
) {
}
