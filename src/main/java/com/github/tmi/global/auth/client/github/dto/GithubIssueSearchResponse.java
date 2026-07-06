package com.github.tmi.global.auth.client.github.dto;

import java.util.List;

public record GithubIssueSearchResponse(
	List<Item> items
) {

	public record Item(
		String title,
		String body
	) {
	}
}
