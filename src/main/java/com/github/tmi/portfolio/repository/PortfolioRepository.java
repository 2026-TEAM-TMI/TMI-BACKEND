package com.github.tmi.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.tmi.portfolio.domain.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
	List<Portfolio> findAllByMemberId(Long memberId);

	List<Portfolio> findAllByMemberIdAndPublished(Long memberId, boolean published);

	long countByMemberId(Long memberId);

	@Query("select coalesce(sum(p.viewsCount), 0) from Portfolio p where p.memberId = :memberId")
	long sumViewsCountByMemberId(@Param("memberId") Long memberId);
}
