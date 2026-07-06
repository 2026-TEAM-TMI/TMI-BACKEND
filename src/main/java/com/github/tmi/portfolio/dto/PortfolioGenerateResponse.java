package com.github.tmi.portfolio.dto;

public record PortfolioGenerateResponse(
	String url
) {

	public static PortfolioGenerateResponse of(final String url) {
		return new PortfolioGenerateResponse(url);
	}
}
