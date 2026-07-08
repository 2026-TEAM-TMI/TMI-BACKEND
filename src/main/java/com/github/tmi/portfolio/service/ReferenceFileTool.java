package com.github.tmi.portfolio.service;

import java.io.ByteArrayInputStream;

import org.apache.tika.Tika;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.github.tmi.file.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceFileTool {

	private static final String ALLOWED_PREFIX = "project-files/";
	private static final int TEXT_MAX = 12000;

	private final Tika tika = new Tika();

	private final FileService fileService;

	@Tool(description = "포트폴리오 프로젝트에 첨부된 참고자료 파일(PDF/PPTX/DOCX/TXT 등)의 내용을 텍스트로 읽어온다.")
	public String readReferenceFile(
		@ToolParam(description = "첨부 파일의 S3 key (예: project-files/3/xxxx.pdf)") final String s3Key
	) {
		if (s3Key == null || !s3Key.startsWith(ALLOWED_PREFIX)) {
			return "허용되지 않은 파일 경로입니다: " + s3Key;
		}

		try {
			byte[] bytes = fileService.getObjectBytes(s3Key);
			String text = tika.parseToString(new ByteArrayInputStream(bytes));

			if (text == null || text.isBlank()) {
				return "파일에서 텍스트를 추출하지 못했습니다: " + s3Key;
			}
			return text.length() <= TEXT_MAX ? text : text.substring(0, TEXT_MAX) + "\n...(생략됨)";
		} catch (Exception e) {
			log.warn("참고자료 읽기 실패. key={}, error={}", s3Key, e.getMessage());
			return "참고자료를 읽지 못했습니다: " + s3Key;
		}
	}
}
