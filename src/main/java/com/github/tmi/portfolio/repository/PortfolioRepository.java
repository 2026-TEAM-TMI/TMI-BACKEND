package com.github.tmi.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.tmi.portfolio.domain.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
