package com.github.tmi.file.dto;

public record PresignedUrlResponse(
	String uploadUrl,
	String key,
	long expiresInSeconds
) {

	public static PresignedUrlResponse of(final String uploadUrl, final String key, final long expiresInSeconds) {
		return new PresignedUrlResponse(uploadUrl, key, expiresInSeconds);
	}
}
