package com.github.tmi.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.tmi.member.domain.Member;
import com.github.tmi.member.domain.enums.SocialType;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsBySocialIdAndSocialType(String socialId, SocialType socialType);

	Optional<Member> findBySocialIdAndSocialType(String socialId, SocialType socialType);
}
