package com.github.tmi.portfolio.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.github.tmi.portfolio.domain.enums.JobCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "portfolio")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Portfolio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = true, length = 1024)
	private String thumbnailImage;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private JobCategory jobCategory;

	@Column(nullable = true)
	private String description;

	@Column(nullable = false)
	private Long viewsCount;

	@Column(nullable = true, length = 1024)
	private String url;

	@Column(nullable = false)
	private boolean published;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private Long memberId;

	public static Portfolio create(
		final String title,
		final String thumbnailImage,
		final JobCategory jobCategory,
		final String description,
		final String url,
		final boolean published,
		final Long memberId
	) {
		return Portfolio.builder()
			.title(title)
			.thumbnailImage(thumbnailImage)
			.jobCategory(jobCategory)
			.description(description)
			.url(url)
			.viewsCount(0L)
			.published(published)
			.memberId(memberId)
			.build();
	}
}
