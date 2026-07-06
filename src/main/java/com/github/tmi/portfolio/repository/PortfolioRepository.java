package com.github.tmi.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.tmi.portfolio.domain.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
	List<Portfolio> findAllByMemberId(Long memberId);

	List<Portfolio> findAllByMemberIdAndPublished(Long memberId, boolean published);
}
