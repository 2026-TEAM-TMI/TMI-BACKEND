package com.github.tmi.portfolio.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.tmi.global.auth.annotation.CurrentMember;
import com.github.tmi.global.response.dto.SuccessResponse;
import com.github.tmi.portfolio.domain.enums.JobCategory;
import com.github.tmi.portfolio.dto.FindPortfolioResponse;
import com.github.tmi.portfolio.dto.PortfolioCreateRequest;
import com.github.tmi.portfolio.dto.PortfolioFeedResponse;
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
		return ResponseEntity.ok(
			SuccessResponse.of(PortfolioSuccessCode.GENERATE_SUCCESS, PortfolioGenerateResponse.of(url)));
	}

	@GetMapping("/feed")
	public ResponseEntity<SuccessResponse<PortfolioFeedResponse>> feed(
		@RequestParam(required = false) final JobCategory jobCategory,
		@PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
	) {
		PortfolioFeedResponse response = portfolioService.findPublicFeed(jobCategory, pageable);
		return ResponseEntity.ok(
			SuccessResponse.of(PortfolioSuccessCode.GET_FEED_SUCCESS, response));
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<SuccessResponse<FindPortfolioResponse>> findPortfolios(
		@CurrentMember final Long loginMemberId,
		@PathVariable final Long memberId
	) {
		FindPortfolioResponse response = portfolioService.findPortfoliosByMemberId(loginMemberId, memberId);
		return ResponseEntity.ok(
			SuccessResponse.of(PortfolioSuccessCode.GET_PORTFOLIOS_SUCCESS, response));
	}
}
