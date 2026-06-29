package com.github.tmi.member.domain.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	MEMBER("ROLE_MEMBER"),
	ADMIN("ROLE_ADMIN"),
	;

	private final String roleName;

	public GrantedAuthority toGrantedAuthority() {
		return new SimpleGrantedAuthority(roleName);
	}
}
