package com.nocodebi.prodservice.utils;

import com.google.gson.Gson;
import com.nocodebi.prodservice.model.AccessToken;
import com.nocodebi.prodservice.model.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Optional;

import static javax.crypto.Cipher.SECRET_KEY;

public class JwtUtil {

    private final Key key;
    private final EncryptionUtil encryptionUtil;
    private final Gson gson;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(Secret.JWT_SECRET.getBytes());
        this.encryptionUtil = new EncryptionUtil(Secret.ENCRYPTION_SECRET);
        this.gson = new Gson();
    }

    /**
     * Validates the given JWT token.
     *
     * @param token The JWT token.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {

        if (token != null && !token.isEmpty()) {

            try {
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (JwtException ex) {
                return false;
            }

        } else {

            return false;

        }

    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token.
     * @return Claims object if valid, null otherwise.
     */
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException ex) {
            return null;
        }
    }

    /**
     * Extracts an AccessToken object from a JWT.
     *
     * @param token The JWT token.
     * @return An AccessToken object if valid, null otherwise.
     */
    public AccessToken getAccessTokenObject(String token) {
        return parseTokenToObject(token, AccessToken.class);
    }

    /**
     * Extracts a RefreshToken object from a JWT.
     *
     * @param token The JWT token.
     * @return A RefreshToken object if valid, null otherwise.
     */
    public RefreshToken getRefreshTokenObject(String token) {
        return parseTokenToObject(token, RefreshToken.class);
    }

    /**
     * Validates a request token.
     *
     * @param token The token string.
     * @return true if valid, false otherwise.
     * @throws IllegalArgumentException If the token is null or empty.
     */
    public boolean validateRequestToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Invalid token provided.");
        }
//        System.out.println("validateRequestToken >>> " + validateToken(token));
        return validateToken(token);
    }

    /**
     * Parses a JWT and decrypts the data into a given object type.
     *
     * @param token The JWT token.
     * @param clazz The class type to deserialize the data into.
     * @return The deserialized object if successful, null otherwise.
     */
    private <T> T parseTokenToObject(String token, Class<T> clazz) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims == null) {
                return null;
            }

            String decryptedData = encryptionUtil.decrypt((String) claims.get("data"));

            return Optional.ofNullable(decryptedData)
                    .map(data -> gson.fromJson(data, clazz))
                    .orElse(null);

        } catch (Exception e) {
            return null;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // ✅ Use parserBuilder() instead of parser()
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // Extracts "sub" (username)
    }

    // 🔹 Helper method to convert SECRET_KEY to proper Key object
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(String.valueOf(SECRET_KEY)); // Ensure it's Base64 encoded
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
