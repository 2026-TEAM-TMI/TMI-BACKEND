package com.github.tmi.portfolio.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.tmi.file.service.FileService;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.member.domain.Member;
import com.github.tmi.member.dto.DashboardResponse;
import com.github.tmi.member.exception.MemberErrorCode;
import com.github.tmi.member.repository.MemberRepository;
import com.github.tmi.portfolio.domain.Portfolio;
import com.github.tmi.portfolio.domain.enums.JobCategory;
import com.github.tmi.portfolio.dto.FindPortfolioResponse;
import com.github.tmi.portfolio.dto.PortfolioCreateRequest;
import com.github.tmi.portfolio.dto.PortfolioDto;
import com.github.tmi.portfolio.dto.PortfolioFeedResponse;
import com.github.tmi.portfolio.repository.PortfolioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioService {

	private final PortfolioAiService portfolioAiService;
	private final FileService fileService;
	private final ThumbnailService thumbnailService;
	private final MemberRepository memberRepository;
	private final PortfolioRepository portfolioRepository;

	public String createPortfolio(final Long memberId, final PortfolioCreateRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new TMIException(MemberErrorCode.MEMBER_NOT_FOUND));

		String html = portfolioAiService.generate(memberId, request);
		String url = fileService.uploadPortfolioHtml(memberId, html);
		String thumbnailUrl = thumbnailService.createThumbnail(memberId, url);

		savePortfolio(member.getId(), url, thumbnailUrl, request);
		return url;
	}

	@Transactional
	public void savePortfolio(
		final Long memberId,
		final String url,
		final String thumbnailUrl,
		final PortfolioCreateRequest request
	) {
		Portfolio portfolio = Portfolio.create(
			request.portfolioTitle(),
			thumbnailUrl,
			request.jobCategory(),
			request.portfolioDescription(),
			url,
			request.isPublic(),
			memberId
		);

		portfolioRepository.save(portfolio);
	}

	@Transactional(readOnly = true)
	public FindPortfolioResponse findPortfoliosByMemberId(Long loginMemberId, Long memberId) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new TMIException(MemberErrorCode.MEMBER_NOT_FOUND));

		boolean isOwner = loginMemberId.equals(memberId);
		List<Portfolio> found = isOwner
			? portfolioRepository.findAllByMemberId(memberId)
			: portfolioRepository.findAllByMemberIdAndPublished(memberId, true);

		List<PortfolioDto> portfolios = found.stream()
			.map(PortfolioDto::from)
			.toList();

		return new FindPortfolioResponse(portfolios);
	}

	@Transactional(readOnly = true)
	public PortfolioFeedResponse findPublicFeed(final JobCategory jobCategory, final Pageable pageable) {
		Page<Portfolio> portfolios = (jobCategory == null)
			? portfolioRepository.findAllByPublished(true, pageable)
			: portfolioRepository.findAllByPublishedAndJobCategory(true, jobCategory, pageable);

		return PortfolioFeedResponse.from(portfolios.map(PortfolioDto::from));
	}

	@Transactional(readOnly = true)
	public DashboardResponse getDashboard(final Long memberId) {
		long portfolioCount = portfolioRepository.countByMemberId(memberId);
		long totalViewsCount = portfolioRepository.sumViewsCountByMemberId(memberId);
		return DashboardResponse.of(portfolioCount, totalViewsCount);
	}
}
