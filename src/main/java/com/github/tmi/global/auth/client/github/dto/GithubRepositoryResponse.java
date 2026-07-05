package com.github.tmi.global.auth.client.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GithubRepositoryResponse(
	Long id,
	String name,
	String fullName,
	@JsonProperty("private") boolean isPrivate,
	String htmlUrl,
	String description,
	String defaultBranch
) {

	public static GithubRepositoryResponse from(final GithubRepositoryApiResponse repo) {
		return new GithubRepositoryResponse(
			repo.id(),
			repo.name(),
			repo.fullName(),
			repo.isPrivate(),
			repo.htmlUrl(),
			repo.description(),
			repo.defaultBranch()
		);
	}
}
