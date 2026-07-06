package com.github.tmi.portfolio.dto;

public record AwardRequest(
	String title,
	String organization,
	String date,
	String description
) {
}
