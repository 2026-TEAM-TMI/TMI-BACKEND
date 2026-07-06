package com.github.tmi.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

	@Value("${spring.ai.openai.api-key}")
	private String apiKey;

	@Value("${spring.ai.openai.base-url}")
	private String baseUrl;

	@Value("${spring.ai.openai.chat.options.model}")
	private String model;

	@Bean
	public OpenAiApi openAiApi() {
		return OpenAiApi.builder()
			.baseUrl(baseUrl)
			.apiKey(apiKey)
			.build();
	}

	@Bean
	public OpenAiChatModel openAiChatModel(final OpenAiApi openAiApi) {
		return OpenAiChatModel.builder()
			.openAiApi(openAiApi)
			.defaultOptions(OpenAiChatOptions.builder()
				.model(model)
				.maxTokens(16384)
				.build())
			.build();
	}

	@Bean
	public ChatClient chatClient(final OpenAiChatModel openAiChatModel) {
		return ChatClient.create(openAiChatModel);
	}
}
