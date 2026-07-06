package com.github.tmi.global.auth.client.github.dto;

import java.util.List;

public record GithubTreeResponse(
	List<TreeEntry> tree
) {

	public record TreeEntry(
		String path,
		String type,
		String sha
	) {
	}
}
