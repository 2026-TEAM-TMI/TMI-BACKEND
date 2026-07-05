package com.github.tmi.portfolio.dto;

import java.util.List;

public record ProjectRequest(
	String name,
	Long repositoryId,
	String description,
	List<String> fileKeys,
	List<String> imageKeys
) {
}
