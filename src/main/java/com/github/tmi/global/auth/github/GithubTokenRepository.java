package com.github.tmi.global.auth.github;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubTokenRepository extends JpaRepository<GithubToken, Long> {
}
