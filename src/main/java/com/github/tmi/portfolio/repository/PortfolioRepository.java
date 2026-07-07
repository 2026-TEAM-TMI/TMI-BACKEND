package com.github.tmi.portfolio.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.tmi.portfolio.domain.Portfolio;
import com.github.tmi.portfolio.domain.enums.JobCategory;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
	List<Portfolio> findAllByMemberId(Long memberId);

	List<Portfolio> findAllByMemberIdAndPublished(Long memberId, boolean published);

	Page<Portfolio> findAllByPublished(boolean published, Pageable pageable);

	Page<Portfolio> findAllByPublishedAndJobCategory(boolean published, JobCategory jobCategory, Pageable pageable);

	long countByMemberId(Long memberId);

	@Query("select coalesce(sum(p.viewsCount), 0) from Portfolio p where p.memberId = :memberId")
	long sumViewsCountByMemberId(@Param("memberId") Long memberId);
}
