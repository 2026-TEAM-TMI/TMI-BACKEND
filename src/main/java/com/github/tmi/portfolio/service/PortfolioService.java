package com.github.tmi.portfolio.service;

import org.springframework.stereotype.Service;

import com.github.tmi.file.service.FileService;
import com.github.tmi.portfolio.dto.PortfolioCreateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioService {

	private final PortfolioAiService portfolioAiService;
	private final FileService fileService;

	public String createPortfolio(final Long memberId, final PortfolioCreateRequest request) {
		String html = portfolioAiService.generate(memberId, request);
		return fileService.uploadPortfolioHtml(memberId, html);
	}
}
