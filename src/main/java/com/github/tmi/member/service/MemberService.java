package com.github.tmi.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.tmi.global.auth.dto.MemberSocialLoginResponse;
import com.github.tmi.global.auth.github.GithubTokenService;
import com.github.tmi.global.exception.TMIException;
import com.github.tmi.member.domain.Member;
import com.github.tmi.member.domain.enums.Role;
import com.github.tmi.member.domain.enums.SocialType;
import com.github.tmi.member.dto.MemberInfoResponse;
import com.github.tmi.member.exception.MemberErrorCode;
import com.github.tmi.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final GithubTokenService githubTokenService;

	@Transactional(readOnly = true)
	public boolean checkMemberExists(final String socialId, final SocialType socialType) {
		return memberRepository.existsBySocialIdAndSocialType(socialId, socialType);
	}

	@Transactional(readOnly = true)
	public Member findMember(final String socialId, final SocialType socialType) {
		return memberRepository.findBySocialIdAndSocialType(socialId, socialType)
			.orElseThrow(() -> new TMIException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public MemberInfoResponse getMyInfo(final Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new TMIException(MemberErrorCode.MEMBER_NOT_FOUND));

		String githubLogin = githubTokenService.getGithubLoginOrNull(memberId);

		return MemberInfoResponse.of(
			githubLogin,
			member.getName(),
			member.getProfileImage(),
			member.getRole(),
			member.getEmail()
		);
	}

	@Transactional
	public Member registerMember(final MemberSocialLoginResponse socialInfo) {
		log.info("Registering new member. socialType={}, socialId={}",
			socialInfo.socialType(), socialInfo.socialId());

		Member member = Member.create(
			socialInfo.email(),
			socialInfo.name(),
			socialInfo.profileImage(),
			socialInfo.socialId(),
			socialInfo.socialType(),
			Role.MEMBER
		);
		return memberRepository.save(member);
	}
}
