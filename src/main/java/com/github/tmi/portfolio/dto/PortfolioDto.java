package com.github.tmi.portfolio.dto;

import com.github.tmi.portfolio.domain.Portfolio;
import com.github.tmi.portfolio.domain.enums.JobCategory;

public record PortfolioDto(
	String portfolioTitle,
	String portfolioDescription,
	String thumbnailImage,
	JobCategory jobCategory,
	Long viewsCount,
	String url
) {

	public static PortfolioDto from(
		Portfolio portfolio
	) {
		return new PortfolioDto(
			portfolio.getTitle(),
			portfolio.getDescription(),
			portfolio.getThumbnailImage(),
			portfolio.getJobCategory(),
			portfolio.getViewsCount(),
			portfolio.getUrl());
	}
}
