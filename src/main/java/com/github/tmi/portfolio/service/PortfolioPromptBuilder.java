package com.github.tmi.portfolio.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.tmi.file.service.FileService;
import com.github.tmi.global.auth.client.github.GithubApiClient;
import com.github.tmi.portfolio.dto.ActivityRequest;
import com.github.tmi.portfolio.dto.AwardRequest;
import com.github.tmi.portfolio.dto.PortfolioCreateRequest;
import com.github.tmi.portfolio.dto.ProjectRequest;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioPromptBuilder {

	private static final String NONE = "없음";

	private final GithubApiClient githubApiClient;
	private final FileService fileService;

	public String build(final PortfolioCreateRequest request, final String authorization, final String githubLogin) {
		StringBuilder sb = new StringBuilder();

		sb.append("## 사용자 기본 정보\n");
		sb.append("- GitHub ID: ").append(orNone(githubLogin)).append('\n');
		sb.append("- 이름: ").append(orNone(request.name())).append('\n');
		sb.append("- 자기소개: ").append(orNone(request.description())).append('\n');
		sb.append("- 관련 분야: ").append(request.jobCategory()).append('\n');
		if (request.address() != null && !request.address().isBlank()) {
			sb.append("- 주소: ").append(request.address()).append('\n');
		}
		sb.append("- 연락처: ").append(formatContact(request.contact())).append('\n');
		sb.append("- 프로필/대표 이미지 URL 목록: ")
			.append(toUrlArray(request.portfolioImageKeys(), fileService::getObjectUrl)).append('\n');
		sb.append("- 수상이력:\n").append(formatAwards(request.awards()));
		sb.append("- 활동이력:\n").append(formatActivities(request.activities()));
		sb.append('\n');

		if (request.customPrompt() != null && !request.customPrompt().isBlank()) {
			sb.append("## 전체 요구사항 (디자인/톤 등)\n").append(request.customPrompt()).append("\n\n");
		}

		List<ProjectRequest> projects = request.projects() == null ? List.of() : request.projects();
		sb.append("## 프로젝트 목록 (").append(projects.size()).append("개)\n\n");

		int index = 1;
		for (ProjectRequest project : projects) {
			sb.append("### 프로젝트 ").append(index++).append(": ").append(orNone(project.name())).append('\n');

			String fullName = resolveFullName(authorization, project.repositoryId());
			if (fullName != null) {
				sb.append("- GitHub 레포지토리(owner/repo): ").append(fullName)
					.append("  ← 도구 호출 시 repoFullName 인자로 사용\n");
			}
			sb.append("- 강조하고 싶은 내용: ").append(orNone(project.description())).append('\n');
			sb.append("- 이미지 URL 목록: ").append(toUrlArray(project.imageKeys(), fileService::getObjectUrl)).append('\n');
			sb.append("- 추가 첨부 파일: ")
				.append(toUrlArrayOrNull(project.fileKeys(), fileService::createPresignedGetUrl)).append("\n\n");
		}

		sb.append("---\n");
		sb.append("각 프로젝트의 레포지토리에 대해 제공된 도구");
		sb.append("(getMyCommitMessages, getMyPullRequests, getReadme, listSourceFiles, readSourceFile)를 호출해\n");
		sb.append("실제 커밋·PR·README·소스 파일을 확인한 뒤, System Prompt의 규칙에 따라 단일 포트폴리오 HTML을 생성하라.\n");

		return sb.toString();
	}

	private String resolveFullName(final String authorization, final Long repositoryId) {
		if (repositoryId == null) {
			return null;
		}
		try {
			return githubApiClient.getRepositoryById(authorization, repositoryId).fullName();
		} catch (FeignException e) {
			log.warn("레포지토리 조회 실패. repositoryId={}, error={}", repositoryId, e.contentUTF8());
			return null;
		}
	}

	private String formatContact(final Map<String, String> contact) {
		if (contact == null || contact.isEmpty()) {
			return NONE;
		}
		return contact.entrySet().stream()
			.map(entry -> entry.getKey() + " " + entry.getValue())
			.collect(Collectors.joining(" / "));
	}

	private String formatAwards(final List<AwardRequest> awards) {
		if (awards == null || awards.isEmpty()) {
			return "  - " + NONE + "\n";
		}
		StringBuilder sb = new StringBuilder();
		for (AwardRequest award : awards) {
			sb.append("  - ").append(joinNonBlank(" / ", award.date(), award.title(), award.description()));
			if (award.organization() != null && !award.organization().isBlank()) {
				sb.append(" (").append(award.organization()).append(')');
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private String formatActivities(final List<ActivityRequest> activities) {
		if (activities == null || activities.isEmpty()) {
			return "  - " + NONE + "\n";
		}
		StringBuilder sb = new StringBuilder();
		for (ActivityRequest activity : activities) {
			sb.append("  - ").append(joinNonBlank(" ", activity.period(), activity.organization()));
			String detail = joinNonBlank(", ", activity.title(), activity.description());
			if (!detail.isBlank()) {
				sb.append(" — ").append(detail);
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private String toUrlArray(final List<String> keys, final Function<String, String> urlMapper) {
		if (keys == null || keys.isEmpty()) {
			return "[]";
		}
		return keys.stream()
			.map(urlMapper)
			.map(url -> "\"" + url + "\"")
			.collect(Collectors.joining(", ", "[", "]"));
	}

	private String toUrlArrayOrNull(final List<String> keys, final Function<String, String> urlMapper) {
		if (keys == null || keys.isEmpty()) {
			return "null";
		}
		return toUrlArray(keys, urlMapper);
	}

	private String orNone(final String value) {
		return (value == null || value.isBlank()) ? NONE : value;
	}

	private String joinNonBlank(final String delimiter, final String... values) {
		return java.util.Arrays.stream(values)
			.filter(Objects::nonNull)
			.filter(value -> !value.isBlank())
			.collect(Collectors.joining(delimiter));
	}
}
