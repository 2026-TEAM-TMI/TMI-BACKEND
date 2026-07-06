package com.github.tmi.file.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadType {

	PROJECT_FILE("project-files"),
	PROJECT_IMAGE("project-images"),
	PORTFOLIO_IMAGE("portfolio-images"),
	;

	private final String directory;
}
