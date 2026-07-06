package com.github.tmi.file.dto;

import com.github.tmi.file.domain.UploadType;

public record PresignedUrlRequest(
	String fileName,      // 원본 파일명 (확장자 추출용)
	String contentType,   // 업로드 시 사용할 Content-Type (PUT 요청 헤더와 동일해야 함)
	UploadType uploadType // 저장 위치 구분
) {
}
