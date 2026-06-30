package com.github.tmi.member.service;

import org.springframework.stereotype.Service;

import com.github.tmi.global.auth.jwt.TokenRepository;
import com.github.tmi.global.auth.jwt.exception.TokenErrorCode;
import com.github.tmi.global.auth.redis.Token;
import com.github.tmi.global.exception.TMIException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberTokenService {

	private final TokenRepository tokenRepository;

	@Transactional
	public void saveRefreshToken(final Long memberId, final String refreshToken) {
		log.info("Saving refresh token for memberId: {}", memberId);
		tokenRepository.save(Token.of(memberId, refreshToken));
		log.info("Successfully saved refresh token for memberId: {}", memberId);
	}

	public Long findIdByRefreshToken(final String refreshToken) {
		log.info("Searching for memberId using refresh token: {}", refreshToken);
		Token token = tokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> {
				log.error("Refresh token not found in Redis: {}", refreshToken);
				return new TMIException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND);
			});
		log.info("Found memberId: {} for refresh token", token.getId());
		return token.getId();
	}

	@Transactional
	public void deleteRefreshToken(final Long memberId) {
		log.info("Deleting refresh token for memberId: {}", memberId);
		Token token = tokenRepository.findById(memberId)
			.orElseThrow(() -> {
				log.error("No refresh token found in Redis for memberId: {}", memberId);
				return new TMIException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND);
			});
		tokenRepository.delete(token);
		log.info("Successfully deleted refresh token for memberId: {}", memberId);
	}
}

