package com.github.tmi.portfolio.dto;

public record PortfolioGenerateResponse(
	String html
) {

	public static PortfolioGenerateResponse of(final String html) {
		return new PortfolioGenerateResponse(html);
	}
}
