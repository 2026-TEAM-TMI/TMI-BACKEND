package com.github.tmi.github.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.tmi.global.auth.client.github.GithubApiClient;
import com.github.tmi.global.auth.client.github.dto.GithubRepositoryApiResponse;
import com.github.tmi.global.auth.client.github.dto.GithubRepositoryResponse;
import com.github.tmi.global.auth.exception.OAuthErrorCode;
import com.github.tmi.global.auth.github.GithubTokenService;
import com.github.tmi.global.exception.TMIException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubRepositoryService {

	private final GithubTokenService githubTokenService;
	private final GithubApiClient githubApiClient;

	// 내 소유(owner) + 직접 협업(collaborator) + 조직 멤버(organization_member)
	private static final String AFFILIATION = "owner,collaborator,organization_member";
	private static final String ORGANIZATION = "Organization";
	private static final int PER_PAGE = 100;
	private static final int MAX_PAGES = 10;

	public List<GithubRepositoryResponse> getMyRepositories(final Long memberId) {
		String authorization = "Bearer " + githubTokenService.getDecryptedToken(memberId);

		try {
			List<GithubRepositoryResponse> result = new ArrayList<>();

			for (int page = 1; page <= MAX_PAGES; page++) {
				List<GithubRepositoryApiResponse> repositories = githubApiClient.getUserRepositories(
					authorization, "all", AFFILIATION, "updated", "desc", PER_PAGE, page);

				repositories.stream()
					.filter(this::isIncluded)
					.map(GithubRepositoryResponse::from)
					.forEach(result::add);

				if (repositories.size() < PER_PAGE) {
					break;
				}
			}
			return result;
		} catch (FeignException e) {
			log.error("Failed to fetch GitHub repositories. Error: {}", e.contentUTF8(), e);
			throw new TMIException(OAuthErrorCode.GET_INFO_ERROR);
		}
	}

	// 개인 레포는 그대로 포함, 조직 레포는 write 이상 권한일 때만 포함
	private boolean isIncluded(final GithubRepositoryApiResponse repo) {
		if (repo.owner() == null || !ORGANIZATION.equals(repo.owner().type())) {
			return true;
		}
		return hasWriteAccess(repo);
	}

	private boolean hasWriteAccess(final GithubRepositoryApiResponse repo) {
		GithubRepositoryApiResponse.Permissions permissions = repo.permissions();
		return permissions != null && (permissions.admin() || permissions.maintain() || permissions.push());
	}
}
