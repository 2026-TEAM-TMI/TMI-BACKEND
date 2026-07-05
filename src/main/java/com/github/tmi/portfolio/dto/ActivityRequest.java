package com.github.tmi.portfolio.dto;

public record ActivityRequest(
	String title,
	String organization,
	String period,
	String description
) {
}