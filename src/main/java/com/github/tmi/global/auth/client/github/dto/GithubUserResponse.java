package com.github.tmi.global.auth.client.github.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GithubUserResponse(
	Long id,
	String login,
	String name,
	String email,
	String avatarUrl
) {
}
