package io.nocodebi.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;  // 16 bytes authentication tag
    private static final int IV_LENGTH_BYTE = 12;     // Recommended 12 bytes IV for GCM

    private final SecretKeySpec secretKeySpec;

    public EncryptionUtil(String secret) {
        byte[] key = secret.getBytes(); // Ensure the secret is exactly 16 bytes for AES-128, 24 bytes for AES-192, or 32 bytes for AES-256
        this.secretKeySpec = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            // Generate a random 12-byte IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // Initialize cipher with IV and tag length
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

            // Perform encryption
            byte[] cipherText = cipher.doFinal(plainText.getBytes());

            // Prepend IV to ciphertext (needed for decryption)
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            byte[] cipherMessage = byteBuffer.array();

            // Return as Base64 encoded string
            return Base64.getEncoder().encodeToString(cipherMessage);
        } catch (Exception e) {
            System.out.println("encrypt >>> Exception >>> " + e.getLocalizedMessage());
            return null;
        }
    }

    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            // Decode the Base64 encoded data
            byte[] cipherMessage = Base64.getDecoder().decode(encryptedText);
            ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);

            // Extract the IV from the beginning
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);

            // Extract the ciphertext
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            // Initialize cipher with the same IV and tag length
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

            // Perform decryption
            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText);
        } catch (Exception e) {
            System.out.println("decrypt >>> Exception >>> " + e.getLocalizedMessage());
            return null;
        }
    }
}
