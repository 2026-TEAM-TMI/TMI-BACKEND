package com.github.tmi.global.auth.client.github;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.tmi.global.auth.client.github.dto.GithubBlobResponse;
import com.github.tmi.global.auth.client.github.dto.GithubCommitResponse;
import com.github.tmi.global.auth.client.github.dto.GithubEmailResponse;
import com.github.tmi.global.auth.client.github.dto.GithubIssueSearchResponse;
import com.github.tmi.global.auth.client.github.dto.GithubRepositoryApiResponse;
import com.github.tmi.global.auth.client.github.dto.GithubTreeResponse;
import com.github.tmi.global.auth.client.github.dto.GithubUserResponse;

@FeignClient(name = "github-api-client", url = "https://api.github.com")
public interface GithubApiClient {

	@GetMapping("/user")
	GithubUserResponse getUserInformation(@RequestHeader("Authorization") String authorization);

	@GetMapping("/user/emails")
	List<GithubEmailResponse> getUserEmails(@RequestHeader("Authorization") String authorization);

	@GetMapping("/user/repos")
	List<GithubRepositoryApiResponse> getUserRepositories(
		@RequestHeader("Authorization") String authorization,
		@RequestParam("visibility") String visibility,
		@RequestParam("affiliation") String affiliation,
		@RequestParam("sort") String sort,
		@RequestParam("direction") String direction,
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page
	);

	@GetMapping("/repositories/{id}")
	GithubRepositoryApiResponse getRepositoryById(
		@RequestHeader("Authorization") String authorization,
		@PathVariable("id") Long id
	);

	@GetMapping("/repos/{owner}/{repo}")
	GithubRepositoryApiResponse getRepository(
		@RequestHeader("Authorization") String authorization,
		@PathVariable("owner") String owner,
		@PathVariable("repo") String repo
	);

	@GetMapping("/repos/{owner}/{repo}/commits")
	List<GithubCommitResponse> getCommits(
		@RequestHeader("Authorization") String authorization,
		@PathVariable("owner") String owner,
		@PathVariable("repo") String repo,
		@RequestParam("author") String author,
		@RequestParam("per_page") int perPage
	);

	@GetMapping("/search/issues")
	GithubIssueSearchResponse searchIssues(
		@RequestHeader("Authorization") String authorization,
		@RequestParam("q") String query,
		@RequestParam("per_page") int perPage
	);

	@GetMapping(value = "/repos/{owner}/{repo}/readme", headers = "Accept=application/vnd.github.raw")
	String getReadme(
		@RequestHeader("Authorization") String authorization,
		@PathVariable("owner") String owner,
		@PathVariable("repo") String repo
	);

	@GetMapping("/repos/{owner}/{repo}/git/trees/{treeSha}")
	GithubTreeResponse getTree(
		@RequestHeader("Authorization") String authorization,
		@PathVariable("owner") String owner,
		@PathVariable("repo") String repo,
		@PathVariable("treeSha") String treeSha,
		@RequestParam("recursive") int recursive
	);

	@GetMapping("/repos/{owner}/{repo}/git/blobs/{fileSha}")
	GithubBlobResponse getBlob(
		@RequestHeader("Authorization") String authorization,
		@PathVariable("owner") String owner,
		@PathVariable("repo") String repo,
		@PathVariable("fileSha") String fileSha
	);
}
