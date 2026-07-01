package com.github.tmi.member.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.github.tmi.global.auth.jwt.JwtTokenProvider;
import com.github.tmi.global.auth.jwt.JwtValidationType;
import com.github.tmi.global.auth.jwt.TokenRepository;
import com.github.tmi.global.auth.jwt.exception.TokenErrorCode;
import com.github.tmi.global.auth.redis.Token;
import com.github.tmi.global.auth.security.MemberAuthentication;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.member.domain.enums.Role;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberTokenService {

	private final TokenRepository tokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

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

	public String reissueAccessToken(final String refreshToken) {
		validateRefreshToken(refreshToken);

		Long memberIdFromToken = jwtTokenProvider.getMemberIdFromJwt(refreshToken);
		Long storedMemberId = findIdByRefreshToken(refreshToken);
		if (!memberIdFromToken.equals(storedMemberId)) {
			throw new TMIException(TokenErrorCode.REFRESH_TOKEN_MEMBER_ID_MISMATCH_ERROR);
		}

		Role role = jwtTokenProvider.getRoleFromJwt(refreshToken);
		Authentication authentication = new MemberAuthentication(
			memberIdFromToken,
			null,
			List.of(role.toGrantedAuthority())
		);
		return jwtTokenProvider.issueAccessToken(authentication);
	}

	private void validateRefreshToken(final String refreshToken) {
		JwtValidationType type = jwtTokenProvider.validateToken(refreshToken);
		switch (type) {
			case VALID_JWT -> {
			}
			case EXPIRED_JWT_TOKEN -> throw new TMIException(TokenErrorCode.REFRESH_TOKEN_EXPIRED_ERROR);
			case INVALID_JWT_SIGNATURE -> throw new TMIException(TokenErrorCode.REFRESH_TOKEN_SIGNATURE_ERROR);
			case UNSUPPORTED_JWT_TOKEN -> throw new TMIException(TokenErrorCode.UNSUPPORTED_REFRESH_TOKEN_ERROR);
			case EMPTY_JWT -> throw new TMIException(TokenErrorCode.REFRESH_TOKEN_EMPTY_ERROR);
			default -> throw new TMIException(TokenErrorCode.INVALID_REFRESH_TOKEN_ERROR);
		}
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
