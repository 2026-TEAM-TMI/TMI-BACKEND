package com.github.tmi.global.auth.github;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.tmi.global.auth.exception.OAuthErrorCode;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.global.util.AesEncryptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubTokenService {

	private final GithubTokenRepository githubTokenRepository;
	private final AesEncryptor aesEncryptor;

	@Transactional
	public void saveOrUpdate(
		final Long memberId,
		final String rawAccessToken,
		final String githubLogin,
		final String scope
	) {
		String encrypted = aesEncryptor.encrypt(rawAccessToken);

		githubTokenRepository.findById(memberId)
			.ifPresentOrElse(
				token -> token.update(encrypted, githubLogin, scope),
				() -> githubTokenRepository.save(GithubToken.of(memberId, encrypted, githubLogin, scope))
			);
		log.info("Stored GitHub access token for memberId: {}", memberId);
	}

	@Transactional(readOnly = true)
	public String getDecryptedToken(final Long memberId) {
		GithubToken token = githubTokenRepository.findById(memberId)
			.orElseThrow(() -> new TMIException(OAuthErrorCode.GITHUB_TOKEN_NOT_FOUND));
		return aesEncryptor.decrypt(token.getAccessToken());
	}
}
