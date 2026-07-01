package com.github.tmi.member.domain;

import com.github.tmi.member.domain.enums.Role;
import com.github.tmi.member.domain.enums.SocialType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = true)
	private String email;

	@Column(nullable = true)
	private String name;

	@Column(nullable = true, length = 1024)
	private String profileImage;

	@Column(nullable = false)
	private String socialId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType socialType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	public static Member create(
		final String email,
		final String name,
		final String profileImage,
		final String socialId,
		final SocialType socialType,
		final Role role
	) {
		return Member.builder()
			.email(email)
			.name(name)
			.profileImage(profileImage)
			.socialId(socialId)
			.socialType(socialType)
			.role(role)
			.build();
	}
}
