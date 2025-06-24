package io.nocodebi.model;

import java.sql.Timestamp;
import java.util.Date;

public class RefreshToken {

    private String tokenId;
    private String userId;
    private String googleTokenId;
    private String googleRefreshToken;
    private Date refreshTokenExpiresAt;

    public String getGoogleTokenId() {
        return googleTokenId;
    }

    public void setGoogleTokenId(String googleTokenId) {
        this.googleTokenId = googleTokenId;
    }

    public String getGoogleRefreshToken() {
        return googleRefreshToken;
    }

    public void setGoogleRefreshToken(String googleRefreshToken) {
        this.googleRefreshToken = googleRefreshToken;
    }

    // Getters and setters
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(Date refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public boolean isValid() {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (refreshTokenExpiresAt != null && now.after(refreshTokenExpiresAt)) {
            System.out.println("refresh token expired");
            return false;
        }

        return true;
    }

}
