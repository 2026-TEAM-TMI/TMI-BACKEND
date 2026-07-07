package com.github.tmi.portfolio.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record PortfolioFeedResponse(
	List<PortfolioDto> portfolios,
	int page,
	int size,
	long totalElements,
	int totalPages,
	boolean hasNext
) {

	public static PortfolioFeedResponse from(final Page<PortfolioDto> page) {
		return new PortfolioFeedResponse(
			page.getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.hasNext()
		);
	}
}
