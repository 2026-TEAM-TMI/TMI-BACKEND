package com.github.tmi.global.auth.client.github.dto;

public record GithubEmailResponse(
	String email,
	boolean primary,
	boolean verified
) {
}
