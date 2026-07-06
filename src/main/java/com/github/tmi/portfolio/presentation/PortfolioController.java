package com.github.tmi.portfolio.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.tmi.global.auth.annotation.CurrentMember;
import com.github.tmi.global.response.dto.SuccessResponse;
import com.github.tmi.portfolio.dto.PortfolioCreateRequest;
import com.github.tmi.portfolio.dto.PortfolioGenerateResponse;
import com.github.tmi.portfolio.exception.PortfolioSuccessCode;
import com.github.tmi.portfolio.service.PortfolioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

	private final PortfolioService portfolioService;

	@PostMapping
	public ResponseEntity<SuccessResponse<PortfolioGenerateResponse>> generate(
		@CurrentMember final Long memberId,
		@RequestBody final PortfolioCreateRequest request
	) {
		String url = portfolioService.createPortfolio(memberId, request);
		portfolioService.savePortfolio(memberId, url, request);
		return ResponseEntity.ok(
			SuccessResponse.of(PortfolioSuccessCode.GENERATE_SUCCESS, PortfolioGenerateResponse.of(url)));
	}
}
