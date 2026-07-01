package com.github.tmi.global.auth.github;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원당 1개의 GitHub 액세스 토큰을 보관. accessToken은 암호화된 상태로 저장된다.
 */
@Entity
@Table(name = "github_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GithubToken {

	@Id
	private Long memberId;

	@Column(nullable = false, length = 1000)
	private String accessToken;

	// GitHub 사용자명
	private String githubLogin;

	private String scope;

	@Builder
	private GithubToken(final Long memberId, final String accessToken, final String githubLogin, final String scope) {
		this.memberId = memberId;
		this.accessToken = accessToken;
		this.githubLogin = githubLogin;
		this.scope = scope;
	}

	public static GithubToken of(
		final Long memberId,
		final String encryptedAccessToken,
		final String githubLogin,
		final String scope
	) {
		return GithubToken.builder()
			.memberId(memberId)
			.accessToken(encryptedAccessToken)
			.githubLogin(githubLogin)
			.scope(scope)
			.build();
	}

	public void update(final String encryptedAccessToken, final String githubLogin, final String scope) {
		this.accessToken = encryptedAccessToken;
		this.githubLogin = githubLogin;
		this.scope = scope;
	}
}
