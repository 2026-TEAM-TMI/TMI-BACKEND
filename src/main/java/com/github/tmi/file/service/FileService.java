package com.github.tmi.file.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.tmi.file.domain.UploadType;
import com.github.tmi.file.dto.PresignedUrlRequest;
import com.github.tmi.file.dto.PresignedUrlResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class FileService {

	private static final Duration EXPIRY = Duration.ofMinutes(5);

	private final S3Presigner s3Presigner;

	@Value("${aws.s3.bucket}")
	private String bucket;

	public PresignedUrlResponse createPresignedPutUrl(final Long memberId, final PresignedUrlRequest request) {
		String key = buildKey(memberId, request.uploadType(), request.fileName());

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(request.contentType())
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(EXPIRY)
			.putObjectRequest(objectRequest)
			.build();

		PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

		return PresignedUrlResponse.of(presigned.url().toString(), key, EXPIRY.getSeconds());
	}

	private String buildKey(final Long memberId, final UploadType uploadType, final String fileName) {
		return "%s/%d/%s%s".formatted(uploadType.getDirectory(), memberId, UUID.randomUUID(), extractExtension(fileName));
	}

	private String extractExtension(final String fileName) {
		if (fileName == null) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex == -1 ? "" : fileName.substring(dotIndex);
	}
}
