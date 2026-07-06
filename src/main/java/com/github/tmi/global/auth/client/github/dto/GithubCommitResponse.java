package com.github.tmi.global.auth.client.github.dto;

public record GithubCommitResponse(
	Commit commit
) {

	public record Commit(
		String message
	) {
	}
}
