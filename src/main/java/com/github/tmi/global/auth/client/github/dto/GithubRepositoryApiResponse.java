package com.github.tmi.global.auth.client.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GithubRepositoryApiResponse(
	Long id,
	String name,
	String fullName,
	@JsonProperty("private") boolean isPrivate,
	String htmlUrl,
	String description,
	String defaultBranch,
	Owner owner,
	Permissions permissions
) {

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record Owner(
		String login,
		String type
	) {
	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record Permissions(
		boolean admin,
		boolean maintain,
		boolean push,
		boolean triage,
		boolean pull
	) {
	}
}
