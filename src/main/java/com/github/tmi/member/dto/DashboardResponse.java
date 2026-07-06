package com.github.tmi.member.dto;

public record DashboardResponse(
	long portfolioCount,
	long totalViewsCount
) {

	public static DashboardResponse of(final long portfolioCount, final long totalViewsCount) {
		return new DashboardResponse(portfolioCount, totalViewsCount);
	}
}
