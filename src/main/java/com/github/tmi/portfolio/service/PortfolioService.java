package com.github.tmi.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.tmi.file.service.FileService;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.member.domain.Member;
import com.github.tmi.member.exception.MemberErrorCode;
import com.github.tmi.member.repository.MemberRepository;
import com.github.tmi.portfolio.domain.Portfolio;
import com.github.tmi.portfolio.dto.PortfolioCreateRequest;
import com.github.tmi.portfolio.repository.PortfolioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioService {

	private final PortfolioAiService portfolioAiService;
	private final FileService fileService;
	private final MemberRepository memberRepository;
	private final PortfolioRepository portfolioRepository;

	@Transactional
	public String createPortfolio(final Long memberId, final PortfolioCreateRequest request) {
		String html = portfolioAiService.generate(memberId, request);
		return fileService.uploadPortfolioHtml(memberId, html);
	}

	@Transactional
	public void savePortfolio(final Long memberId, final String url, final PortfolioCreateRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new TMIException(MemberErrorCode.MEMBER_NOT_FOUND));

		Portfolio portfolio = Portfolio.create(
			request.portfolioTitle(),
			request.jobCategory(),
			request.portfolioDescription(),
			url,
			request.isPublic(),
			member.getId()
		);

		portfolioRepository.save(portfolio);
	}
}
