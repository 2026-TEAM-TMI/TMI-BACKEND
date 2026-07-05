package com.github.tmi.file.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.tmi.file.dto.PresignedUrlRequest;
import com.github.tmi.file.dto.PresignedUrlResponse;
import com.github.tmi.file.exception.FileSuccessCode;
import com.github.tmi.file.service.FileService;
import com.github.tmi.global.auth.annotation.CurrentMember;
import com.github.tmi.global.response.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@PostMapping("/presigned-url")
	public ResponseEntity<SuccessResponse<PresignedUrlResponse>> createPresignedUrl(
		@CurrentMember final Long memberId,
		@RequestBody final PresignedUrlRequest request
	) {
		PresignedUrlResponse response = fileService.createPresignedPutUrl(memberId, request);
		return ResponseEntity.ok(
			SuccessResponse.of(FileSuccessCode.CREATE_PRESIGNED_URL_SUCCESS, response));
	}
}
