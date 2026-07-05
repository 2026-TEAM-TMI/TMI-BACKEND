package com.github.tmi.portfolio.dto;

import java.util.List;
import java.util.Map;

import com.github.tmi.portfolio.domain.enums.JobCategory;

public record PortfolioCreateRequest(

	// 1. 개인정보
	String name,
	Map<String, String> contact,
	String address,
	String description,
	JobCategory jobCategory,

	// 2. 프로젝트
	List<ProjectRequest> projects,

	// 3. 수상 내역
	List<AwardRequest> awards,

	// 4. 활동 이력
	List<ActivityRequest> activities
) {
}
