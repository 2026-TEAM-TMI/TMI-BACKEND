package com.github.tmi.global.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES-256/GCM 기반 양방향 암복호화.
 * 출력 포맷: Base64( IV(12B) + ciphertext+tag )
 */
@Component
public class AesEncryptor {

	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH_BIT = 128;

	private final SecretKey secretKey;
	private final SecureRandom secureRandom = new SecureRandom();

	public AesEncryptor(@Value("${encryption.secret}") final String secret) {
		try {
			byte[] keyBytes = MessageDigest.getInstance("SHA-256")
				.digest(secret.getBytes(StandardCharsets.UTF_8));
			this.secretKey = new SecretKeySpec(keyBytes, "AES");
		} catch (Exception e) {
			throw new IllegalStateException("AES 키 초기화 실패", e);
		}
	}

	public String encrypt(final String plainText) {
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
			byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			byte[] combined = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new IllegalStateException("암호화 실패", e);
		}
	}

	public String decrypt(final String encrypted) {
		try {
			byte[] combined = Base64.getDecoder().decode(encrypted);

			byte[] iv = new byte[IV_LENGTH];
			System.arraycopy(combined, 0, iv, 0, IV_LENGTH);

			byte[] cipherText = new byte[combined.length - IV_LENGTH];
			System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.length);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

			return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new IllegalStateException("복호화 실패", e);
		}
	}
}
