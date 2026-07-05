package com.github.tmi.file.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadType {

	PROJECT_FILE("project-files"),       // 프로젝트 관련 추가 파일 (pdf, pptx, txt 등)
	PROJECT_IMAGE("project-images"),     // 프로젝트 사진 (jpeg, png, pdf)
	PORTFOLIO_IMAGE("portfolio-images"), // 포트폴리오에 첨부할 사진
	;

	private final String directory;
}
