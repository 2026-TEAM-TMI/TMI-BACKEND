package com.github.tmi.portfolio.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.github.tmi.global.auth.client.github.GithubApiClient;
import com.github.tmi.global.auth.client.github.dto.GithubBlobResponse;
import com.github.tmi.global.auth.client.github.dto.GithubCommitResponse;
import com.github.tmi.global.auth.client.github.dto.GithubIssueSearchResponse;
import com.github.tmi.global.auth.client.github.dto.GithubTreeResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubTools {

	public static final String CTX_AUTHORIZATION = "authorization";
	public static final String CTX_GITHUB_LOGIN = "githubLogin";

	private static final int COMMIT_LIMIT = 100;
	private static final int PR_LIMIT = 30;
	private static final int PR_BODY_MAX = 500;
	private static final int README_MAX = 4000;
	private static final int FILE_MAX = 8000;
	private static final int FILE_LIST_LIMIT = 300;
	private static final String BLOB = "blob";

	private final GithubApiClient githubApiClient;

	@Tool(description = "지정한 GitHub 레포지토리에서 로그인 사용자 본인이 작성한 커밋 메시지 목록을 최신순으로 가져온다.")
	public String getMyCommitMessages(
		@ToolParam(description = "owner/repo 형식의 레포지토리 전체 이름 (예: Team-Kiero/Kiero-Server)") final String repoFullName,
		final ToolContext toolContext
	) {
		try {
			String[] or = ownerRepo(repoFullName);
			List<GithubCommitResponse> commits = githubApiClient.getCommits(
				auth(toolContext), or[0], or[1], login(toolContext), COMMIT_LIMIT);

			String result = commits.stream()
				.map(c -> c.commit() == null ? "" : firstLine(c.commit().message()))
				.filter(m -> !m.isBlank())
				.collect(Collectors.joining("\n"));
			return result.isBlank() ? "커밋 내역이 없습니다." : result;
		} catch (FeignException e) {
			return errorMessage("커밋", repoFullName, e);
		}
	}

	@Tool(description = "지정한 GitHub 레포지토리에서 로그인 사용자 본인이 작성한 PR의 제목과 본문을 가져온다.")
	public String getMyPullRequests(
		@ToolParam(description = "owner/repo 형식의 레포지토리 전체 이름") final String repoFullName,
		final ToolContext toolContext
	) {
		try {
			String query = "repo:%s is:pr author:%s".formatted(repoFullName, login(toolContext));
			GithubIssueSearchResponse response = githubApiClient.searchIssues(auth(toolContext), query, PR_LIMIT);

			if (response.items() == null || response.items().isEmpty()) {
				return "PR 내역이 없습니다.";
			}
			return response.items().stream()
				.map(item -> "제목: " + nullToEmpty(item.title())
					+ "\n본문: " + truncate(nullToEmpty(item.body()), PR_BODY_MAX))
				.collect(Collectors.joining("\n---\n"));
		} catch (FeignException e) {
			return errorMessage("PR", repoFullName, e);
		}
	}

	@Tool(description = "지정한 GitHub 레포지토리의 README 내용을 가져온다.")
	public String getReadme(
		@ToolParam(description = "owner/repo 형식의 레포지토리 전체 이름") final String repoFullName,
		final ToolContext toolContext
	) {
		try {
			String[] or = ownerRepo(repoFullName);
			String readme = githubApiClient.getReadme(auth(toolContext), or[0], or[1]);
			return readme == null || readme.isBlank() ? "README가 없습니다." : truncate(readme, README_MAX);
		} catch (FeignException e) {
			return errorMessage("README", repoFullName, e);
		}
	}

	@Tool(description = "지정한 GitHub 레포지토리의 소스 파일 경로 목록을 가져온다. 특정 파일 내용을 읽기 전에 어떤 파일이 있는지 확인하는 용도.")
	public String listSourceFiles(
		@ToolParam(description = "owner/repo 형식의 레포지토리 전체 이름") final String repoFullName,
		final ToolContext toolContext
	) {
		try {
			String[] or = ownerRepo(repoFullName);
			String branch = defaultBranch(toolContext, or);
			GithubTreeResponse tree = githubApiClient.getTree(auth(toolContext), or[0], or[1], branch, 1);

			return tree.tree().stream()
				.filter(entry -> BLOB.equals(entry.type()))
				.map(GithubTreeResponse.TreeEntry::path)
				.limit(FILE_LIST_LIMIT)
				.collect(Collectors.joining("\n"));
		} catch (FeignException e) {
			return errorMessage("파일 목록", repoFullName, e);
		}
	}

	@Tool(description = "지정한 GitHub 레포지토리에서 특정 경로의 소스 파일 내용을 읽어온다. 경로는 listSourceFiles로 먼저 확인한다.")
	public String readSourceFile(
		@ToolParam(description = "owner/repo 형식의 레포지토리 전체 이름") final String repoFullName,
		@ToolParam(description = "읽을 파일의 경로 (예: src/main/java/App.java)") final String path,
		final ToolContext toolContext
	) {
		try {
			String[] or = ownerRepo(repoFullName);
			String branch = defaultBranch(toolContext, or);
			GithubTreeResponse tree = githubApiClient.getTree(auth(toolContext), or[0], or[1], branch, 1);

			String sha = tree.tree().stream()
				.filter(entry -> BLOB.equals(entry.type()) && path.equals(entry.path()))
				.map(GithubTreeResponse.TreeEntry::sha)
				.findFirst()
				.orElse(null);
			if (sha == null) {
				return "해당 경로의 파일을 찾을 수 없습니다: " + path;
			}

			GithubBlobResponse blob = githubApiClient.getBlob(auth(toolContext), or[0], or[1], sha);
			return truncate(decodeBase64(blob.content()), FILE_MAX);
		} catch (FeignException e) {
			return errorMessage("파일 읽기", repoFullName, e);
		}
	}

	private String defaultBranch(final ToolContext toolContext, final String[] ownerRepo) {
		return githubApiClient.getRepository(auth(toolContext), ownerRepo[0], ownerRepo[1]).defaultBranch();
	}

	private String auth(final ToolContext toolContext) {
		return (String) toolContext.getContext().get(CTX_AUTHORIZATION);
	}

	private String login(final ToolContext toolContext) {
		return (String) toolContext.getContext().get(CTX_GITHUB_LOGIN);
	}

	private String[] ownerRepo(final String repoFullName) {
		int slash = repoFullName == null ? -1 : repoFullName.indexOf('/');
		if (slash <= 0) {
			throw new IllegalArgumentException("레포지토리 이름은 owner/repo 형식이어야 합니다: " + repoFullName);
		}
		return new String[] {repoFullName.substring(0, slash), repoFullName.substring(slash + 1)};
	}

	private String decodeBase64(final String content) {
		if (content == null || content.isBlank()) {
			return "";
		}
		return new String(Base64.getMimeDecoder().decode(content), StandardCharsets.UTF_8);
	}

	private String firstLine(final String message) {
		if (message == null) {
			return "";
		}
		int newline = message.indexOf('\n');
		return (newline == -1 ? message : message.substring(0, newline)).strip();
	}

	private String truncate(final String value, final int max) {
		if (value == null) {
			return "";
		}
		return value.length() <= max ? value : value.substring(0, max) + "\n...(생략됨)";
	}

	private String nullToEmpty(final String value) {
		return value == null ? "" : value;
	}

	private String errorMessage(final String what, final String repoFullName, final FeignException e) {
		log.warn("{} 조회 실패. repo={}, status={}, body={}", what, repoFullName, e.status(), e.contentUTF8());
		return "%s 데이터를 가져오지 못했습니다 (repo=%s, status=%d).".formatted(what, repoFullName, e.status());
	}
}
