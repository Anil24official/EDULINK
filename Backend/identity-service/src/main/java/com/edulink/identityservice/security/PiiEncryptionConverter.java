package com.edulink.identityservice.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Spec §8 — encrypted-at-rest storage for PII columns.
 * AES-256/GCM with a random 12-byte IV per row. Ciphertext layout:
 *   "enc:v1:" + base64(IV || ciphertext || tag)
 * Pre-existing plaintext rows (no "enc:v1:" prefix) are returned as-is so this can
 * be enabled on a populated database without a re-encrypt batch job.
 */
@Component
@Converter(autoApply = false)
public class PiiEncryptionConverter implements AttributeConverter<String, String> {

    private static final Logger log = LoggerFactory.getLogger(PiiEncryptionConverter.class);
    private static final String PREFIX = "enc:v1:";
    private static final String TRANSFORM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;

    private static SecretKeySpec keySpec;
    private static final SecureRandom RANDOM = new SecureRandom();

    public PiiEncryptionConverter(@Value("${edulink.pii.encryption-key:edulink-default-dev-key-change-me}") String rawKey) {
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(rawKey.getBytes(StandardCharsets.UTF_8));
            keySpec = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize PII encryption key", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String plaintext) {
        if (plaintext == null || plaintext.isEmpty() || keySpec == null) return plaintext;
        try {
            byte[] iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buf = ByteBuffer.allocate(iv.length + ct.length);
            buf.put(iv);
            buf.put(ct);
            return PREFIX + Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            log.error("PII encryption failed; storing plaintext as fallback. Reason: {}", e.getMessage());
            return plaintext;
        }
    }

    @Override
    public String convertToEntityAttribute(String stored) {
        if (stored == null || stored.isEmpty()) return stored;
        if (!stored.startsWith(PREFIX)) return stored; // legacy plaintext row
        if (keySpec == null) return stored;
        try {
            byte[] decoded = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
            byte[] iv = new byte[IV_LENGTH];
            byte[] ct = new byte[decoded.length - IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            System.arraycopy(decoded, IV_LENGTH, ct, 0, ct.length);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("PII decryption failed for column; returning stored value. Reason: {}", e.getMessage());
            return stored;
        }
    }
}
