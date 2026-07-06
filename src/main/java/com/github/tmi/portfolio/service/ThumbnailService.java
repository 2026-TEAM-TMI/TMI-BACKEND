package com.github.tmi.portfolio.service;

import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.tmi.file.service.FileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

	private static final int VIEWPORT_WIDTH = 1280;
	private static final int VIEWPORT_HEIGHT = 800;
	private static final int IMAGE_QUALITY = 80;

	private final RestClient restClient = RestClient.builder()
		.requestFactory(ClientHttpRequestFactories.get(
			ClientHttpRequestFactorySettings.DEFAULTS
				.withConnectTimeout(Duration.ofSeconds(5))
				.withReadTimeout(Duration.ofSeconds(30))))
		.build();

	private final FileService fileService;

	@Value("${thumbnail.screenshot.api-url}")
	private String apiUrl;

	@Value("${thumbnail.screenshot.access-key}")
	private String accessKey;

	public String createThumbnail(final Long memberId, final String portfolioUrl) {
		URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
			.queryParam("access_key", accessKey)
			.queryParam("url", portfolioUrl)
			.queryParam("format", "jpg")
			.queryParam("viewport_width", VIEWPORT_WIDTH)
			.queryParam("viewport_height", VIEWPORT_HEIGHT)
			.queryParam("full_page", false)
			.queryParam("image_quality", IMAGE_QUALITY)
			.build()
			.encode()
			.toUri();

		byte[] image = restClient.get()
			.uri(uri)
			.retrieve()
			.body(byte[].class);

		return fileService.uploadThumbnail(memberId, image);
	}
}
