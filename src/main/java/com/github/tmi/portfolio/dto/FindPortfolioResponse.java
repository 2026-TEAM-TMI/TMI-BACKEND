package com.github.tmi.portfolio.dto;

import java.util.List;

public record FindPortfolioResponse(
	List<PortfolioDto> portfolios
) {
}
